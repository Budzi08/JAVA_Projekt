package com.example.bike_rental.service;

import com.example.bike_rental.model.Bike;
import com.example.bike_rental.model.BikeType;
import com.example.bike_rental.model.BikeStatus;
import com.example.bike_rental.repository.BikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class BikeService {

    private final BikeRepository bikeRepository;

    public BikeService(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    public void addBike(String model, BikeType type, int productionYear, double rentalPrice, BikeStatus status, MultipartFile image) throws IOException {
        String imageUrl = saveImage(image);

        Bike bike = new Bike(model, type, productionYear, rentalPrice, status, imageUrl);

        bikeRepository.save(bike);
    }

    private String saveImage(MultipartFile image) throws IOException {
        if (image.isEmpty()) {
            return null;
        }

        String uploadDir = "images";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(image.getInputStream(), filePath);

        return "/images/" + fileName;
    }
}
