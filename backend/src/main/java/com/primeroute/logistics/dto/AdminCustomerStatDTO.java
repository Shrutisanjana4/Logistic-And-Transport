package com.primeroute.logistics.dto;

public class AdminCustomerStatDTO {
    private String name;
    private long orders;

    public AdminCustomerStatDTO() {}
    public AdminCustomerStatDTO(String name, long orders) {
        this.name = name;
        this.orders = orders;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getOrders() { return orders; }
    public void setOrders(long orders) { this.orders = orders; }
}
