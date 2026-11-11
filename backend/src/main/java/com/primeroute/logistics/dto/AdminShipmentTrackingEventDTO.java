package com.primeroute.logistics.dto;

import java.time.LocalDateTime;

public class AdminShipmentTrackingEventDTO {
    private Long id;
    private String status;
    private String title;
    private String description;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private LocalDateTime eventTime;
    private boolean completed;
    private boolean current;

    public AdminShipmentTrackingEventDTO() {}

    public AdminShipmentTrackingEventDTO(Long id, String status, String title, String description,
                                        String locationName, Double latitude, Double longitude,
                                        LocalDateTime eventTime, boolean completed, boolean current) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.description = description;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.eventTime = eventTime;
        this.completed = completed;
        this.current = current;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public boolean isCurrent() { return current; }
    public void setCurrent(boolean current) { this.current = current; }
}
