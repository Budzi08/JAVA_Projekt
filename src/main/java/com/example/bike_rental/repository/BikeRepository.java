package com.example.bike_rental.repository;

import com.example.bike_rental.model.Bike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BikeRepository extends JpaRepository<Bike, Long> {
    List<Bike> findByType(String type);
    Optional<Bike> findByModelAndType(String model, String type);
}
