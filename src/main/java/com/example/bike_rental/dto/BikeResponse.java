package com.example.bike_rental.dto;

public class BikeResponse {
    private String model;
    private String type;
    private int productionYear;
    private double rentalPrice;

    public BikeResponse(String model, String type, int productionYear, double rentalPrice) {
        this.model = model;
        this.type = type;
        this.productionYear = productionYear;
        this.rentalPrice = rentalPrice;
    }

    // Gettery i settery
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    public double getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(double rentalPrice) {
        this.rentalPrice = rentalPrice;
    }
}

