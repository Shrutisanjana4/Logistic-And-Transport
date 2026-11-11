package com.primeroute.logistics.dto;

public class PublicContactInfoDTO {
    private String supportEmail;
    private String supportPhone;
    private String availability;
    private String description;
    private String companyName;
    private String companyAddress;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String website;

    public PublicContactInfoDTO() {}

    public PublicContactInfoDTO(String supportEmail, String supportPhone, String availability, String description,
                                String companyName, String companyAddress, String city, String state,
                                String country, String postalCode, String website) {
        this.supportEmail = supportEmail;
        this.supportPhone = supportPhone;
        this.availability = availability;
        this.description = description;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.website = website;
    }

    public String getSupportEmail() { return supportEmail; }
    public void setSupportEmail(String supportEmail) { this.supportEmail = supportEmail; }

    public String getSupportPhone() { return supportPhone; }
    public void setSupportPhone(String supportPhone) { this.supportPhone = supportPhone; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}
