package com.example.bike_rental.service;

import com.example.bike_rental.dto.RentalRequest;
import com.example.bike_rental.model.Bike;
import com.example.bike_rental.model.Rental;
import com.example.bike_rental.model.User;
import com.example.bike_rental.repository.BikeRepository;
import com.example.bike_rental.repository.RentalRepository;
import com.example.bike_rental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalService {

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    public List<String> getBikeModelsByType(String type) {
        List<Bike> bikes = bikeRepository.findByType(type);
        return bikes.stream().map(Bike::getModel).collect(Collectors.toList());
    }

    public void addRental(RentalRequest rentalRequest, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Użytkownik nie został znaleziony"));

        Bike bike = bikeRepository.findById(rentalRequest.getBikeId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono roweru o podanym ID"));

        Rental rental = new Rental();
        rental.setBike(bike);
        rental.setUser(user); // Ustaw użytkownika
        rental.setStartDateTime(rentalRequest.getStartDateTime());
        rental.setEndDateTime(rentalRequest.getEndDateTime());

        rentalRepository.save(rental);
    }

    public List<Rental> getRentalsByUsername(String username) {
        return rentalRepository.findByUserUsername(username);
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }
}
