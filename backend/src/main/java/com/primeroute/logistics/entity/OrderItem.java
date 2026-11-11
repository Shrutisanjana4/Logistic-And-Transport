package com.primeroute.logistics.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_weight", nullable = false)
    private Double unitWeight;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "total_weight")
    private Double totalWeight;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public OrderItem() {}

    public OrderItem(Order order, Product product, Integer quantity, Double unitWeight, Double unitPrice) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitWeight = unitWeight != null ? unitWeight : (product != null ? product.getUnitWeight() : 1.0);
        this.unitPrice = unitPrice != null ? unitPrice : (product != null ? product.getUnitPrice() : 0.0);
        this.totalWeight = this.unitWeight * this.quantity;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.totalWeight == null && this.unitWeight != null && this.quantity != null) {
            this.totalWeight = this.unitWeight * this.quantity;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        if (this.unitWeight != null && this.quantity != null) {
            this.totalWeight = this.unitWeight * this.quantity;
        }
    }

    public Double getUnitWeight() { return unitWeight; }
    public void setUnitWeight(Double unitWeight) {
        this.unitWeight = unitWeight;
        if (this.unitWeight != null && this.quantity != null) {
            this.totalWeight = this.unitWeight * this.quantity;
        }
    }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Double getTotalWeight() { return totalWeight; }
    public void setTotalWeight(Double totalWeight) { this.totalWeight = totalWeight; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
