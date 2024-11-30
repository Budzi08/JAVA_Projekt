package com.example.bike_rental.controller;

import com.example.bike_rental.dto.RentalRequest;
import com.example.bike_rental.model.Bike;
import com.example.bike_rental.model.Rental;
import com.example.bike_rental.model.User;
import com.example.bike_rental.repository.BikeRepository;
import com.example.bike_rental.repository.RentalRepository;
import com.example.bike_rental.service.RentalService;
import com.example.bike_rental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/rent")
    public ResponseEntity<?> rentBike(@RequestBody RentalRequest rentalRequest, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());

        Bike bike = bikeRepository.findById(rentalRequest.getBikeId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono roweru"));

        Rental rental = new Rental(
                bike,
                user,
                rentalRequest.getStartDateTime(),
                rentalRequest.getEndDateTime()
        );

        rentalRepository.save(rental);

        return ResponseEntity.ok(Map.of("message", "Wypożyczono rower!"));
    }

    @Autowired
    private RentalService rentalService;

    @GetMapping("/user")
    public ResponseEntity<List<Rental>> getUserRentals(Authentication authentication) {
        List<Rental> rentals = rentalService.getRentalsByUsername(authentication.getName());
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<Rental>> getAllRentals() {
        List<Rental> rentals = rentalService.getAllRentals();
        return ResponseEntity.ok(rentals);
    }
}
