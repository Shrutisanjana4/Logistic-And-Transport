package com.primeroute.logistics.dto.driver;

import java.time.LocalDateTime;

public class ProofOfDeliveryDTO {
    private Long id;
    private Long shipmentId;
    private String trackingNumber;
    private String recipientName;
    private String receivedBy;
    private String signatureData;
    private String signatureBase64;
    private String photoData;
    private String photoUrl;
    private String deliveryNotes;
    private String notes;
    private String deliveredAt;
    private String deliveredBy;
    private Double latitude;
    private Double longitude;

    public ProofOfDeliveryDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getShipmentId() { return shipmentId; }
    public void setShipmentId(Long shipmentId) { this.shipmentId = shipmentId; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getReceivedBy() { return receivedBy; }
    public void setReceivedBy(String receivedBy) { this.receivedBy = receivedBy; }

    public String getSignatureData() { return signatureData; }
    public void setSignatureData(String signatureData) {
        this.signatureData = signatureData;
        this.signatureBase64 = signatureData;
    }

    public String getSignatureBase64() { return signatureBase64; }
    public void setSignatureBase64(String signatureBase64) {
        this.signatureBase64 = signatureBase64;
        this.signatureData = signatureBase64;
    }

    public String getPhotoData() { return photoData; }
    public void setPhotoData(String photoData) {
        this.photoData = photoData;
        this.photoUrl = photoData;
    }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        this.photoData = photoUrl;
    }

    public String getDeliveryNotes() { return deliveryNotes; }
    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
        this.notes = deliveryNotes;
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) {
        this.notes = notes;
        this.deliveryNotes = notes;
    }

    public String getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(String deliveredAt) { this.deliveredAt = deliveredAt; }

    public String getDeliveredBy() { return deliveredBy; }
    public void setDeliveredBy(String deliveredBy) { this.deliveredBy = deliveredBy; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}