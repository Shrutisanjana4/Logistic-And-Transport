package com.primeroute.logistics.dto.warehouse;

import java.time.LocalDateTime;

public class WarehouseTrackingEventDTO {
    private Long id;
    private String status;
    private String title;
    private String description;
    private String locationName;
    private LocalDateTime eventTime;

    public WarehouseTrackingEventDTO() {}

    public WarehouseTrackingEventDTO(Long id, String status, String title, String description, String locationName, LocalDateTime eventTime) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.description = description;
        this.locationName = locationName;
        this.eventTime = eventTime;
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

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
}
