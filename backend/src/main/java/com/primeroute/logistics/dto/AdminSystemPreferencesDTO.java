package com.primeroute.logistics.dto;

public class AdminSystemPreferencesDTO {
    private String defaultCurrency;
    private String defaultDeliveryType;
    private String dateFormat;
    private String timeFormat;
    private String timeZone;

    public AdminSystemPreferencesDTO() {}

    public String getDefaultCurrency() { return defaultCurrency; }
    public void setDefaultCurrency(String defaultCurrency) { this.defaultCurrency = defaultCurrency; }

    public String getDefaultDeliveryType() { return defaultDeliveryType; }
    public void setDefaultDeliveryType(String defaultDeliveryType) { this.defaultDeliveryType = defaultDeliveryType; }

    public String getDateFormat() { return dateFormat; }
    public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }

    public String getTimeFormat() { return timeFormat; }
    public void setTimeFormat(String timeFormat) { this.timeFormat = timeFormat; }

    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }
}
