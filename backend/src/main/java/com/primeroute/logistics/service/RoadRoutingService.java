package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.driver.DriverRouteStepDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

@Service
public class RoadRoutingService {

    private static final Logger log = LoggerFactory.getLogger(RoadRoutingService.class);
    private final RestTemplate restTemplate;

    public static class RouteCalculationResult {
        private final double distanceKm;
        private final int durationMinutes;
        private final List<List<Double>> geometry; // [[lat, lon], ...]
        private final List<DriverRouteStepDTO> steps;
        private final boolean realRoadGeometry;

        public RouteCalculationResult(double distanceKm, int durationMinutes, List<List<Double>> geometry, List<DriverRouteStepDTO> steps, boolean realRoadGeometry) {
            this.distanceKm = Math.round(distanceKm * 10.0) / 10.0;
            this.durationMinutes = Math.max(1, durationMinutes);
            this.geometry = geometry != null ? geometry : new ArrayList<>();
            this.steps = steps != null ? steps : new ArrayList<>();
            this.realRoadGeometry = realRoadGeometry;
        }

        public double getDistanceKm() { return distanceKm; }
        public int getDurationMinutes() { return durationMinutes; }
        public List<List<Double>> getGeometry() { return geometry; }
        public List<DriverRouteStepDTO> getSteps() { return steps; }
        public boolean isRealRoadGeometry() { return realRoadGeometry; }
    }

    public RoadRoutingService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(4).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(4).toMillis());
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Calculates road route between start and destination coordinates.
     */
    public RouteCalculationResult calculateRoute(double startLat, double startLon, double endLat, double endLon) {
        // Fast validation
        if (Math.abs(startLat - endLat) < 0.0001 && Math.abs(startLon - endLon) < 0.0001) {
            List<List<Double>> singlePt = new ArrayList<>();
            singlePt.add(Arrays.asList(startLat, startLon));
            List<DriverRouteStepDTO> steps = new ArrayList<>();
            steps.add(new DriverRouteStepDTO("Arrived at destination", "arrive", 0.0, 0, "ARRIVE"));
            return new RouteCalculationResult(0.0, 0, singlePt, steps, true);
        }

        // 1. Try public OSRM Driving Router
        try {
            String url = String.format(Locale.US,
                    "https://router.project-osrm.org/route/v1/driving/%.6f,%.6f;%.6f,%.6f?overview=full&geometries=geojson&steps=true",
                    startLon, startLat, endLon, endLat);

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map body = response.getBody();
                if ("Ok".equalsIgnoreCase(String.valueOf(body.get("code")))) {
                    List routes = (List) body.get("routes");
                    if (routes != null && !routes.isEmpty()) {
                        Map firstRoute = (Map) routes.get(0);
                        double distanceMeters = Double.parseDouble(String.valueOf(firstRoute.get("distance")));
                        double durationSeconds = Double.parseDouble(String.valueOf(firstRoute.get("duration")));

                        Map geometryMap = (Map) firstRoute.get("geometry");
                        List<List<Double>> roadCoords = new ArrayList<>();
                        if (geometryMap != null && geometryMap.containsKey("coordinates")) {
                            List coords = (List) geometryMap.get("coordinates");
                            for (Object ptObj : coords) {
                                List pt = (List) ptObj;
                                double lon = Double.parseDouble(String.valueOf(pt.get(0)));
                                double lat = Double.parseDouble(String.valueOf(pt.get(1)));
                                roadCoords.add(Arrays.asList(lat, lon));
                            }
                        }

                        // Steps
                        List<DriverRouteStepDTO> stepsList = new ArrayList<>();
                        List legs = (List) firstRoute.get("legs");
                        if (legs != null && !legs.isEmpty()) {
                            Map leg = (Map) legs.get(0);
                            List steps = (List) leg.get("steps");
                            if (steps != null) {
                                for (Object stepObj : steps) {
                                    Map s = (Map) stepObj;
                                    String name = s.get("name") != null && !String.valueOf(s.get("name")).isBlank()
                                            ? String.valueOf(s.get("name")) : "Transit corridor";
                                    double sDist = Double.parseDouble(String.valueOf(s.get("distance"))) / 1000.0;
                                    int sDur = (int) Double.parseDouble(String.valueOf(s.get("duration")));
                                    
                                    Map maneuver = (Map) s.get("maneuver");
                                    String mType = maneuver != null ? String.valueOf(maneuver.get("type")) : "turn";
                                    String mMod = maneuver != null && maneuver.get("modifier") != null ? String.valueOf(maneuver.get("modifier")) : "";
                                    
                                    String instruction = buildInstructionText(mType, mMod, name, sDist);
                                    String iconType = determineIconType(mType, mMod);
                                    
                                    stepsList.add(new DriverRouteStepDTO(
                                            instruction,
                                            mType + (mMod.isEmpty() ? "" : " " + mMod),
                                            Math.round(sDist * 10.0) / 10.0,
                                            sDur,
                                            iconType
                                    ));
                                }
                            }
                        }

                        if (!roadCoords.isEmpty()) {
                            double distKm = distanceMeters / 1000.0;
                            int durMin = (int) Math.ceil(durationSeconds / 60.0);
                            return new RouteCalculationResult(distKm, durMin, roadCoords, stepsList, true);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("OSRM routing call failed ({}, {}) to ({}, {}): {}", startLat, startLon, endLat, endLon, ex.getMessage());
        }

        // 2. Intelligent Road-like Spline Interpolation Fallback
        return calculateInterpolatedFallback(startLat, startLon, endLat, endLon);
    }

    public double calculateHaversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 6371.0 * c;
    }

    private RouteCalculationResult calculateInterpolatedFallback(double startLat, double startLon, double endLat, double endLon) {
        double directKm = calculateHaversineDistanceKm(startLat, startLon, endLat, endLon);
        // Commercial road distance factor is ~1.22x direct air distance in Indian highway corridors
        double roadKm = directKm * 1.22;
        // Average commercial fleet speed is 42 km/h
        int durationMinutes = (int) Math.ceil((roadKm / 42.0) * 60.0);

        List<List<Double>> points = new ArrayList<>();
        int segments = Math.max(12, Math.min(40, (int) (directKm * 1.5)));

        // Generate curved realistic route points along the corridor
        double perpLat = -(endLon - startLon);
        double perpLon = (endLat - startLat);
        double perpLen = Math.sqrt(perpLat * perpLat + perpLon * perpLon);
        if (perpLen > 0) {
            perpLat /= perpLen;
            perpLon /= perpLen;
        }

        for (int i = 0; i <= segments; i++) {
            double t = (double) i / segments;
            // Base linear interpolation
            double lat = startLat + t * (endLat - startLat);
            double lon = startLon + t * (endLon - startLon);

            // Add smooth sine curve deviation to emulate road geometry
            double curveOffset = Math.sin(t * Math.PI) * 0.008 + Math.sin(t * 3 * Math.PI) * 0.003;
            lat += perpLat * curveOffset;
            lon += perpLon * curveOffset;

            points.add(Arrays.asList(lat, lon));
        }

        List<DriverRouteStepDTO> fallbackSteps = new ArrayList<>();
        fallbackSteps.add(new DriverRouteStepDTO("Head towards primary logistics highway corridor", "depart", Math.round((roadKm * 0.15) * 10.0) / 10.0, (int) (durationMinutes * 0.15 * 60), "DEPART"));
        fallbackSteps.add(new DriverRouteStepDTO("Continue straight on National Express Highway corridor", "straight", Math.round((roadKm * 0.60) * 10.0) / 10.0, (int) (durationMinutes * 0.60 * 60), "STRAIGHT"));
        fallbackSteps.add(new DriverRouteStepDTO("Take exit ramp towards destination logistics zone", "fork right", Math.round((roadKm * 0.15) * 10.0) / 10.0, (int) (durationMinutes * 0.15 * 60), "FORK"));
        fallbackSteps.add(new DriverRouteStepDTO("Arrive at recipient address delivery bay", "arrive", Math.round((roadKm * 0.10) * 10.0) / 10.0, (int) (durationMinutes * 0.10 * 60), "ARRIVE"));

        return new RouteCalculationResult(roadKm, durationMinutes, points, fallbackSteps, false);
    }

    private String buildInstructionText(String type, String modifier, String streetName, double distKm) {
        String distStr = distKm >= 1.0 ? String.format(Locale.US, "%.1f km", distKm) : String.format(Locale.US, "%d m", (int)(distKm * 1000));
        if ("depart".equalsIgnoreCase(type)) {
            return "Head on " + streetName + " (" + distStr + ")";
        } else if ("arrive".equalsIgnoreCase(type)) {
            return "Arrive at destination: " + streetName;
        } else if ("fork".equalsIgnoreCase(type)) {
            return "Take the " + (modifier.isEmpty() ? "fork" : modifier + " fork") + " onto " + streetName + " (" + distStr + ")";
        } else if ("roundabout".equalsIgnoreCase(type)) {
            return "At roundabout, exit onto " + streetName + " (" + distStr + ")";
        } else if ("turn".equalsIgnoreCase(type) || "end of road".equalsIgnoreCase(type)) {
            String mod = modifier.isEmpty() ? "turn" : "Turn " + modifier;
            return mod + " onto " + streetName + " (" + distStr + ")";
        }
        return "Continue on " + streetName + " (" + distStr + ")";
    }

    private String determineIconType(String type, String modifier) {
        if ("depart".equalsIgnoreCase(type)) return "DEPART";
        if ("arrive".equalsIgnoreCase(type)) return "ARRIVE";
        if (modifier.contains("right")) return "TURN_RIGHT";
        if (modifier.contains("left")) return "TURN_LEFT";
        if (modifier.contains("fork") || "fork".equalsIgnoreCase(type)) return "FORK";
        return "STRAIGHT";
    }
}
