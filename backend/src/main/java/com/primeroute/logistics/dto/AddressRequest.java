package com.primeroute.logistics.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddressRequest {

    @NotBlank(message = "Location type is required.")
    @JsonAlias({"locationType"})
    private String addressType;

    @NotBlank(message = "Enter a valid contact name.")
    @Size(min = 2, max = 100, message = "Contact name must be between 2 and 100 characters.")
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])[a-zA-Z\\s.'-]{2,100}$",
        message = "Enter a valid contact name."
    )
    @JsonAlias({"contactPerson"})
    private String contactName;

    @NotBlank(message = "Enter a valid phone number.")
    @Pattern(
        regexp = "^(?:\\+91[\\s-]?)?[6-9](?:[\\s-]?\\d){9}$",
        message = "Enter a valid 10-digit Indian mobile number (e.g. 9876543210)."
    )
    @JsonAlias({"contactPhone"})
    private String phone;

    @NotBlank(message = "Enter a valid street / building address.")
    @Size(min = 5, max = 250, message = "Street address must be between 5 and 250 characters.")
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9\\s,.\\-/#/()']{5,250}$",
        message = "Enter a valid street / building address."
    )
    @JsonAlias({"streetAddress", "street"})
    private String addressLine1;

    @Size(max = 150, message = "Apartment / landmark cannot exceed 150 characters.")
    @Pattern(
        regexp = "^$|^[a-zA-Z0-9\\s,.\\-/#/()']{1,150}$",
        message = "Enter a valid landmark or apartment details."
    )
    @JsonAlias({"apartmentUnitLandmark", "landmark", "apartment"})
    private String addressLine2;

    @NotBlank(message = "Enter a valid city name.")
    @Size(min = 2, max = 100, message = "City cannot exceed 100 characters.")
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])[a-zA-Z\\s.'-]{2,100}$",
        message = "Enter a valid city name."
    )
    private String city;

    @NotBlank(message = "Select a valid state.")
    @Size(min = 2, max = 100, message = "State cannot exceed 100 characters.")
    private String state;

    @NotBlank(message = "Postal code is required.")
    @Pattern(
        regexp = "^[1-9][0-9]{5}$",
        message = "Postal code must be a valid 6-digit PIN code starting with digits 1-9."
    )
    private String postalCode;

    @NotBlank(message = "Country is required.")
    @Size(max = 100, message = "Country cannot exceed 100 characters.")
    private String country;

    @JsonAlias({"isDefault"})
    private boolean defaultAddress;

    public AddressRequest() {
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType != null ? addressType.trim() : null;
    }

    public void setLocationType(String locationType) {
        setAddressType(locationType);
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName != null ? contactName.trim() : null;
    }

    public void setContactPerson(String contactPerson) {
        setContactName(contactPerson);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone.trim() : null;
    }

    public void setContactPhone(String contactPhone) {
        setPhone(contactPhone);
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1 != null ? addressLine1.trim() : null;
    }

    public void setStreetAddress(String streetAddress) {
        setAddressLine1(streetAddress);
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2 != null ? addressLine2.trim() : null;
    }

    public void setApartmentUnitLandmark(String apartmentUnitLandmark) {
        setAddressLine2(apartmentUnitLandmark);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city != null ? city.trim() : null;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state != null ? state.trim() : null;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode != null ? postalCode.trim() : null;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country != null ? country.trim() : null;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public void setIsDefault(boolean isDefault) {
        setDefaultAddress(isDefault);
    }
}
