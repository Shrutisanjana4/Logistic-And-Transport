package com.primeroute.logistics.dto.warehouse;

public class UpdateWarehouseDetailsRequest {

    private String operatingHours;
    private String contactPhone;
    private String contactEmail;
    private String managerPhone;

    public UpdateWarehouseDetailsRequest() {}

    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getManagerPhone() { return managerPhone; }
    public void setManagerPhone(String managerPhone) { this.managerPhone = managerPhone; }
}
