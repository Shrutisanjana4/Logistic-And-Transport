package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.Faq;

public class FaqDTO {
    private Long id;
    private String category;
    private String q;
    private String a;
    private Integer displayOrder;
    private Boolean isActive;

    public FaqDTO() {}

    public FaqDTO(Faq faq) {
        this.id = faq.getId();
        this.category = faq.getCategory();
        this.q = faq.getQuestion();
        this.a = faq.getAnswer();
        this.displayOrder = faq.getDisplayOrder();
        this.isActive = faq.getIsActive();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }

    public String getA() { return a; }
    public void setA(String a) { this.a = a; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
