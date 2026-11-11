package com.primeroute.logistics.dto.driver;

public class DriverTrackingEventDTO {
    private Long id;
    private String status;
    private String title;
    private String description;
    private String location;
    private String timestamp;
    private Double latitude;
    private Double longitude;

    public DriverTrackingEventDTO() {}

    public DriverTrackingEventDTO(Long id, String status, String title, String description, String location, String timestamp, Double latitude, Double longitude) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.description = description;
        this.location = location;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
