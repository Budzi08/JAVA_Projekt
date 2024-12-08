package com.example.bike_rental;

import com.example.bike_rental.model.User;
import com.example.bike_rental.repository.BikeRepository;
import com.example.bike_rental.repository.RentalRepository;
import com.example.bike_rental.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RentalRepository rentalRepository;


    @BeforeEach
    void setup() {
        rentalRepository.deleteAll();
        userRepository.deleteAll();
        bikeRepository.deleteAll();
    }

    @Test
    void fullFunctionalityTest() throws Exception {
        // Rejestracja użytkownika
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "username": "testuser",
                        "email": "testuser@example.com",
                        "password": "password123",
                        "confirmPassword": "password123",
                        "firstName": "Test",
                        "lastName": "User"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Użytkownik pomyslnie zarejestrowany"));

        // Logowanie użytkownika
        String token = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "username": "testuser",
                        "password": "password123"
                    }
                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"token\":\"(.*?)\".*", "$1");

        // Dodanie roweru (przygotowanie danych do wypożyczenia)
        mockMvc.perform(post("/api/bikes/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "model": "Model1",
                        "type": "GÓRSKI",
                        "productionYear": 2023,
                        "rentalPrice": 80.0
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Rower został dodany!"));

        Long bikeId = bikeRepository.findAll().get(0).getId();

        mockMvc.perform(post("/api/rentals/rent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(
                                """
                                {
                                    "bikeId": %d,
                                    "startDateTime": "2024-12-03T10:00:00",
                                    "endDateTime": "2024-12-03T12:00:00"
                                }
                                """.formatted(bikeId)
                        ))
                .andExpect(status().isOk())
                .andExpect(content().string("Wypożyczono rower!"));

        // Pobranie listy wypożyczeń użytkownika
        mockMvc.perform(get("/api/rentals/user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].bike.model").value("Model1"))
                .andExpect(jsonPath("$[0].startDateTime").value("2024-12-03T10:00:00"))
                .andExpect(jsonPath("$[0].endDateTime").value("2024-12-03T12:00:00"));

        // Weryfikacja w bazie danych
        assertThat(userRepository.findByUsername("testuser")).isPresent();
        assertThat(bikeRepository.findAll()).hasSize(1);
    }
}
