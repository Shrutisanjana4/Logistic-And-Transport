package com.primeroute.logistics.dto.driver;

public class DriverVehicleDTO {
    private Long id;
    private String vehicleNumber;
    private String vehicleCode;
    private String registrationNumber;
    private String plateNumber;
    private String licensePlate;
    private String vehicleType;
    private String type;
    private String manufacturer;
    private String model;
    private Double capacity;
    private Double capacityTons;
    private String capacityUnit;
    private String status;
    private String currentLocation;
    private Long assignedWarehouseId;
    private String assignedWarehouseCode;
    private String assignedWarehouseName;
    private String lastInspectionDate;
    private String registrationExpiry;
    private String insuranceExpiry;
    private String pucExpiry;
    private int fuelLevelPercent;
    private int odometerKm;
    private int engineTempCelsius;
    private int tirePressurePsi;

    public DriverVehicleDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleCode() { return vehicleCode; }
    public void setVehicleCode(String vehicleCode) { this.vehicleCode = vehicleCode; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Double getCapacity() { return capacity; }
    public void setCapacity(Double capacity) { this.capacity = capacity; }

    public Double getCapacityTons() { return capacityTons; }
    public void setCapacityTons(Double capacityTons) { this.capacityTons = capacityTons; }

    public String getCapacityUnit() { return capacityUnit; }
    public void setCapacityUnit(String capacityUnit) { this.capacityUnit = capacityUnit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public Long getAssignedWarehouseId() { return assignedWarehouseId; }
    public void setAssignedWarehouseId(Long assignedWarehouseId) { this.assignedWarehouseId = assignedWarehouseId; }

    public String getAssignedWarehouseCode() { return assignedWarehouseCode; }
    public void setAssignedWarehouseCode(String assignedWarehouseCode) { this.assignedWarehouseCode = assignedWarehouseCode; }

    public String getAssignedWarehouseName() { return assignedWarehouseName; }
    public void setAssignedWarehouseName(String assignedWarehouseName) { this.assignedWarehouseName = assignedWarehouseName; }

    public String getLastInspectionDate() { return lastInspectionDate; }
    public void setLastInspectionDate(String lastInspectionDate) { this.lastInspectionDate = lastInspectionDate; }

    public String getRegistrationExpiry() { return registrationExpiry; }
    public void setRegistrationExpiry(String registrationExpiry) { this.registrationExpiry = registrationExpiry; }

    public String getInsuranceExpiry() { return insuranceExpiry; }
    public void setInsuranceExpiry(String insuranceExpiry) { this.insuranceExpiry = insuranceExpiry; }

    public String getPucExpiry() { return pucExpiry; }
    public void setPucExpiry(String pucExpiry) { this.pucExpiry = pucExpiry; }

    public int getFuelLevelPercent() { return fuelLevelPercent; }
    public void setFuelLevelPercent(int fuelLevelPercent) { this.fuelLevelPercent = fuelLevelPercent; }

    public int getOdometerKm() { return odometerKm; }
    public void setOdometerKm(int odometerKm) { this.odometerKm = odometerKm; }

    public int getEngineTempCelsius() { return engineTempCelsius; }
    public void setEngineTempCelsius(int engineTempCelsius) { this.engineTempCelsius = engineTempCelsius; }

    public int getTirePressurePsi() { return tirePressurePsi; }
    public void setTirePressurePsi(int tirePressurePsi) { this.tirePressurePsi = tirePressurePsi; }
}
