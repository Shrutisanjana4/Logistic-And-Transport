package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.FulfillmentAllocationMethod;
import com.primeroute.logistics.entity.FulfillmentAllocationStatus;
import com.primeroute.logistics.entity.Warehouse;

public class FulfillmentEvaluationResultDTO {

    private Long orderId;
    private String orderNumber;
    private Long selectedWarehouseId;
    private String selectedWarehouseCode;
    private String selectedWarehouseName;
    private FulfillmentAllocationStatus allocationStatus;
    private FulfillmentAllocationMethod allocationMethod;
    private Double allocationScore;
    private Double distanceKm;
    private String reason;
    private Integer eligibleWarehouseCount;

    public FulfillmentEvaluationResultDTO() {}

    public FulfillmentEvaluationResultDTO(
            Long orderId,
            String orderNumber,
            Long selectedWarehouseId,
            String selectedWarehouseCode,
            String selectedWarehouseName,
            FulfillmentAllocationStatus allocationStatus,
            FulfillmentAllocationMethod allocationMethod,
            Double allocationScore,
            Double distanceKm,
            String reason,
            Integer eligibleWarehouseCount) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.selectedWarehouseId = selectedWarehouseId;
        this.selectedWarehouseCode = selectedWarehouseCode;
        this.selectedWarehouseName = selectedWarehouseName;
        this.allocationStatus = allocationStatus;
        this.allocationMethod = allocationMethod;
        this.allocationScore = allocationScore;
        this.distanceKm = distanceKm;
        this.reason = reason;
        this.eligibleWarehouseCount = eligibleWarehouseCount;
    }

    public static FulfillmentEvaluationResultDTO exception(Long orderId, String orderNumber, String reason) {
        return new FulfillmentEvaluationResultDTO(
                orderId,
                orderNumber,
                null,
                null,
                null,
                FulfillmentAllocationStatus.EXCEPTION,
                FulfillmentAllocationMethod.AUTOMATIC,
                null,
                null,
                reason,
                0
        );
    }

    public static FulfillmentEvaluationResultDTO allocated(
            Long orderId,
            String orderNumber,
            Warehouse warehouse,
            Double score,
            Double distanceKm,
            String reason,
            int eligibleCount) {
        return new FulfillmentEvaluationResultDTO(
                orderId,
                orderNumber,
                warehouse != null ? warehouse.getId() : null,
                warehouse != null ? warehouse.getWarehouseCode() : null,
                warehouse != null ? warehouse.getWarehouseName() : null,
                FulfillmentAllocationStatus.ALLOCATED,
                FulfillmentAllocationMethod.AUTOMATIC,
                score,
                distanceKm,
                reason,
                eligibleCount
        );
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getSelectedWarehouseId() {
        return selectedWarehouseId;
    }

    public void setSelectedWarehouseId(Long selectedWarehouseId) {
        this.selectedWarehouseId = selectedWarehouseId;
    }

    public String getSelectedWarehouseCode() {
        return selectedWarehouseCode;
    }

    public void setSelectedWarehouseCode(String selectedWarehouseCode) {
        this.selectedWarehouseCode = selectedWarehouseCode;
    }

    public String getSelectedWarehouseName() {
        return selectedWarehouseName;
    }

    public void setSelectedWarehouseName(String selectedWarehouseName) {
        this.selectedWarehouseName = selectedWarehouseName;
    }

    public FulfillmentAllocationStatus getAllocationStatus() {
        return allocationStatus;
    }

    public void setAllocationStatus(FulfillmentAllocationStatus allocationStatus) {
        this.allocationStatus = allocationStatus;
    }

    public FulfillmentAllocationMethod getAllocationMethod() {
        return allocationMethod;
    }

    public void setAllocationMethod(FulfillmentAllocationMethod allocationMethod) {
        this.allocationMethod = allocationMethod;
    }

    public Double getAllocationScore() {
        return allocationScore;
    }

    public void setAllocationScore(Double allocationScore) {
        this.allocationScore = allocationScore;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getEligibleWarehouseCount() {
        return eligibleWarehouseCount;
    }

    public void setEligibleWarehouseCount(Integer eligibleWarehouseCount) {
        this.eligibleWarehouseCount = eligibleWarehouseCount;
    }

    @Override
    public String toString() {
        return "FulfillmentEvaluationResultDTO{" +
                "orderId=" + orderId +
                ", orderNumber='" + orderNumber + '\'' +
                ", selectedWarehouseId=" + selectedWarehouseId +
                ", selectedWarehouseCode='" + selectedWarehouseCode + '\'' +
                ", selectedWarehouseName='" + selectedWarehouseName + '\'' +
                ", allocationStatus=" + allocationStatus +
                ", allocationMethod=" + allocationMethod +
                ", allocationScore=" + allocationScore +
                ", distanceKm=" + distanceKm +
                ", reason='" + reason + '\'' +
                ", eligibleWarehouseCount=" + eligibleWarehouseCount +
                '}';
    }
}
