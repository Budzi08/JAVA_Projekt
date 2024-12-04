package com.example.bike_rental;
import com.example.bike_rental.model.Bike;
import com.example.bike_rental.model.Rental;
import com.example.bike_rental.model.User;
import com.example.bike_rental.repository.BikeRepository;
import com.example.bike_rental.repository.RentalRepository;
import com.example.bike_rental.repository.UserRepository;
import com.example.bike_rental.service.BikeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BikeRepository bikeRepository;
    @Autowired
    private RentalRepository rentalRepository;

    @BeforeEach
    void setUp() {
        // Czyszczenie bazy danych przed każdym testem
        userRepository.deleteAll();
        rentalRepository.deleteAll();
        bikeRepository.deleteAll();
        userRepository.deleteAll();

        // Tworzenie przykładowych danych w bazie
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@gmail.com");
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        user.setPassword("haslo");
        user.setRole("ROLE_USER");
    
        userRepository.save(user);


    }

    @Test
    void getAllUsersTest() throws Exception {
        // Wysłanie żądania GET i sprawdzenie wyniku
        mockMvc.perform(get("/api/users/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userRepository.findAll())));
    }

    @Test
    void deleteUserTestSuccess() throws Exception {
        // Pobranie ID użytkownika z bazy
        User user = userRepository.findAll().get(0);

        // Wysłanie żądania DELETE i sprawdzenie wyniku
        mockMvc.perform(delete("/api/users/delete/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Użytkownik został usunięty!"));

        // Weryfikacja, że użytkownik został usunięty
        Optional<User> deletedUser = userRepository.findById(user.getId());
        assert deletedUser.isEmpty();
    }

    @Test
    void deleteUserTestNotFound() throws Exception {
        // Wysłanie żądania DELETE dla nieistniejącego użytkownika
        mockMvc.perform(delete("/api/users/delete/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Użytkownik o podanym ID nie istnieje"));
    }

}