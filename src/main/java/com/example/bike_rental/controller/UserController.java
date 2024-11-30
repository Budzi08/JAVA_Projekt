package com.example.bike_rental.controller;

import com.example.bike_rental.model.User;
import com.example.bike_rental.repository.RentalRepository;
import com.example.bike_rental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RentalRepository rentalRepository;

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Użytkownik o podanym ID nie istnieje"));

        if (rentalRepository.existsByUser(user)) {
            return ResponseEntity.badRequest().body("Nie można usunąć użytkownika, ponieważ ma wypożyczenia.");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("Użytkownik został usunięty!");
    }
}
