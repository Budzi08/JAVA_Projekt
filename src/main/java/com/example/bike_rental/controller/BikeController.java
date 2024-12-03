package com.example.bike_rental.controller;

import com.example.bike_rental.dto.BikeRequest;
import com.example.bike_rental.model.Bike;
import com.example.bike_rental.model.BikeType;
import com.example.bike_rental.dto.BikeResponse;
import com.example.bike_rental.repository.BikeRepository;
import com.example.bike_rental.repository.RentalRepository;
import com.example.bike_rental.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bikes")
public class BikeController {

    @Autowired
    private BikeService bikeService;
    @Autowired
    private BikeRepository bikeRepository;
    @Autowired
    private RentalRepository rentalRepository;


    @GetMapping
    public ResponseEntity<List<BikeResponse>> getAllBikes() {
        List<BikeResponse> bikes = bikeService.getAllBikes();
        return ResponseEntity.ok(bikes);
    }


    @PostMapping("/add")
    public ResponseEntity<String> addBike(@RequestBody BikeRequest bikeRequest) {
        try {
            BikeType bikeType = BikeType.valueOf(bikeRequest.getType().toUpperCase());

            bikeService.addBike(
                    bikeRequest.getModel(),
                    bikeType,
                    bikeRequest.getProductionYear(),
                    bikeRequest.getRentalPrice()
            );
            return ResponseEntity.ok("Rower został dodany!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Nieprawidłowy typ roweru.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wystąpił błąd podczas dodawania roweru: " + e.getMessage());
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBike(@PathVariable Long id) {
        var bike = bikeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rower o podanym ID nie istnieje"));

        if (rentalRepository.existsByBike(bike)) {
            return ResponseEntity.badRequest().body("Nie można usunąć roweru, ponieważ ma wypożyczenia.");
        }

        bikeRepository.deleteById(id);
        return ResponseEntity.ok("Rower został usunięty!");
    }


    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editBike(
            @PathVariable Long id,
            @RequestBody Bike bikeDetails) {

        Optional<Bike> bikeOptional = bikeRepository.findById(id);

        if (bikeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Rower o podanym ID nie istnieje.");
        }

        Bike bike = bikeOptional.get();
        bike.setModel(bikeDetails.getModel());
        bike.setType(bikeDetails.getType());
        bike.setProductionYear(bikeDetails.getProductionYear());
        bike.setRentalPrice(bikeDetails.getRentalPrice());

        bikeRepository.save(bike);

        return ResponseEntity.ok("Rower został zaktualizowany.");
    }
}
