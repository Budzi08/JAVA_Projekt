package com.example.bike_rental.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bikes")
public class Bike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String model;

    @Enumerated(EnumType.STRING)
    private BikeType type; // Typ roweru jako enum

    private int productionYear;

    private double rentalPrice;

    @Enumerated(EnumType.STRING)
    private BikeStatus status; // Status roweru jako enum

    private String imageUrl;

    // Konstruktor bezargumentowy (dla JPA)
    public Bike() {}

    // Konstruktor z parametrami
    public Bike(String model, BikeType type, int productionYear, double rentalPrice, BikeStatus status, String imageUrl) {
        this.model = model;
        this.type = type;
        this.productionYear = productionYear;
        this.rentalPrice = rentalPrice;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    // Gettery i settery
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BikeType getType() {
        return type;
    }

    public void setType(BikeType type) {
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

    public BikeStatus getStatus() {
        return status;
    }

    public void setStatus(BikeStatus status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
