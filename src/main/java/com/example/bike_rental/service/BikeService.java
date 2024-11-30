package com.example.bike_rental.service;

import com.example.bike_rental.model.Bike;
import com.example.bike_rental.model.BikeType;
import com.example.bike_rental.repository.BikeRepository;
import com.example.bike_rental.dto.BikeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BikeService {

    @Autowired
    private BikeRepository bikeRepository;

    public void addBike(String model, BikeType type, int productionYear, double rentalPrice) {
        Bike bike = new Bike();
        bike.setModel(model);
        bike.setType(type);
        bike.setProductionYear(productionYear);
        bike.setRentalPrice(rentalPrice);

        bikeRepository.save(bike);
    }

    public List<BikeResponse> getAllBikes() {
        return bikeRepository.findAll().stream()
                .map(bike -> new BikeResponse(
                        bike.getModel(),
                        bike.getType().toString(),
                        bike.getProductionYear(),
                        bike.getRentalPrice()
                ))
                .collect(Collectors.toList());
    }

}
