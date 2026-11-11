package com.primeroute.logistics.dto;

public class AdminNeedsAttentionDTO {
    private String key;
    private String title;
    private long count;
    private String description;
    private String severity;
    private String link;

    public AdminNeedsAttentionDTO() {}

    public AdminNeedsAttentionDTO(String key, String title, long count, String description, String severity, String link) {
        this.key = key;
        this.title = title;
        this.count = count;
        this.description = description;
        this.severity = severity;
        this.link = link;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}
