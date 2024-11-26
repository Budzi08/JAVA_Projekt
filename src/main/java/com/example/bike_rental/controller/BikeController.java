package com.example.bike_rental.controller;

import com.example.bike_rental.model.BikeStatus;
import com.example.bike_rental.model.BikeType;
import com.example.bike_rental.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/bikes")
public class BikeController {

    @Autowired
    private BikeService bikeService;

    @PostMapping("/add")
    public String addBike(@RequestParam("model") String model,
                          @RequestParam("type") String type,
                          @RequestParam("productionYear") int productionYear,
                          @RequestParam("rentalPrice") double rentalPrice,
                          @RequestParam("status") String status,
                          @RequestParam("image") MultipartFile image) {

        BikeType bikeType = BikeType.valueOf(type.toUpperCase());
        BikeStatus bikeStatus = BikeStatus.valueOf(status.toUpperCase());

        try {
            bikeService.addBike(model, bikeType, productionYear, rentalPrice, bikeStatus, image);
            return "Rower został dodany pomyślnie!";
        } catch (IOException e) {
            return "Wystąpił błąd podczas dodawania roweru: " + e.getMessage();
        }
    }
}
