package com.example.bike_rental.repository;

import com.example.bike_rental.model.Bike;
import com.example.bike_rental.model.Rental;
import com.example.bike_rental.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByUserUsername(String username);
    boolean existsByUser(User user);
    boolean existsByBike(Bike bike);
}
