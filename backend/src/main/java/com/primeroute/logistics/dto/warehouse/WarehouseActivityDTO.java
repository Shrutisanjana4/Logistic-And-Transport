package com.primeroute.logistics.dto.warehouse;

import java.time.LocalDateTime;

public class WarehouseActivityDTO {

    private String who;
    private String what;
    private String where;
    private String whenFormatted;
    private LocalDateTime timestamp;
    private String type;
    private String reference;

    public WarehouseActivityDTO() {}

    public WarehouseActivityDTO(String who, String what, String where, String whenFormatted, LocalDateTime timestamp, String type, String reference) {
        this.who = who;
        this.what = what;
        this.where = where;
        this.whenFormatted = whenFormatted;
        this.timestamp = timestamp;
        this.type = type;
        this.reference = reference;
    }

    public String getWho() { return who; }
    public void setWho(String who) { this.who = who; }

    public String getWhat() { return what; }
    public void setWhat(String what) { this.what = what; }

    public String getWhere() { return where; }
    public void setWhere(String where) { this.where = where; }

    public String getWhenFormatted() { return whenFormatted; }
    public void setWhenFormatted(String whenFormatted) { this.whenFormatted = whenFormatted; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
}
