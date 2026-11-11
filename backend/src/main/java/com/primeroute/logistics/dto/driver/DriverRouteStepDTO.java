package com.primeroute.logistics.dto.driver;

public class DriverRouteStepDTO {
    private String instruction;
    private String maneuver;
    private Double distanceKm;
    private Integer durationSeconds;
    private String iconType;

    public DriverRouteStepDTO() {}

    public DriverRouteStepDTO(String instruction, String maneuver, Double distanceKm, Integer durationSeconds, String iconType) {
        this.instruction = instruction;
        this.maneuver = maneuver;
        this.distanceKm = distanceKm;
        this.durationSeconds = durationSeconds;
        this.iconType = iconType;
    }

    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }

    public String getManeuver() { return maneuver; }
    public void setManeuver(String maneuver) { this.maneuver = maneuver; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

    public String getIconType() { return iconType; }
    public void setIconType(String iconType) { this.iconType = iconType; }
}
