package com.example.bike_rental.service;

import com.example.bike_rental.dto.RentalRequest;
import com.example.bike_rental.model.Bike;
import com.example.bike_rental.model.Rental;
import com.example.bike_rental.model.User;
import com.example.bike_rental.repository.BikeRepository;
import com.example.bike_rental.repository.RentalRepository;
import com.example.bike_rental.repository.UserRepository;
import com.example.bike_rental.util.QRCodeGenerator;
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
    @Autowired
    private EmailService emailService;

    public List<String> getBikeModelsByType(String type) {
        List<Bike> bikes = bikeRepository.findByType(type);
        return bikes.stream().map(Bike::getModel).collect(Collectors.toList());
    }

    public void addRental(RentalRequest rentalRequest, String username) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Użytkwnik nie został znaleziony"));

        Bike bike = bikeRepository.findById(rentalRequest.getBikeId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono roweru o podanym ID"));

        Rental rental = new Rental();
        rental.setBike(bike);
        rental.setUser(user);
        rental.setStartDateTime(rentalRequest.getStartDateTime());
        rental.setEndDateTime(rentalRequest.getEndDateTime());

        rentalRepository.save(rental);


        String rentalData = String.format(
                "Wypożyczenie: ID=%d, Użytkownik=%s, Rower=%s, Od=%s, Do=%s",
                rental.getId(), user.getUsername(), bike.getModel(),
                rental.getStartDateTime(), rental.getEndDateTime()
        );

        byte[] qrCode = QRCodeGenerator.generateQRCode(rentalData, 300, 300);

        emailService.sendEmailWithQRCode(
                user.getEmail(),
                "Potwierdzenie wypożyczenia roweru",
                "Twoje wypożyczenie zostało potwierdzone",
                qrCode
        );
    }

    public List<Rental> getRentalsByUsername(String username) {
        return rentalRepository.findByUserUsername(username);
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }
}
