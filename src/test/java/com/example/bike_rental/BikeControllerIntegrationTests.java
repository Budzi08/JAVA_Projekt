
package com.example.bike_rental;

import com.example.bike_rental.controller.BikeController;
import com.example.bike_rental.dto.BikeRequest;
import com.example.bike_rental.dto.BikeResponse;
import com.example.bike_rental.model.Bike;
import com.example.bike_rental.model.BikeType;
import com.example.bike_rental.model.Rental;
import com.example.bike_rental.model.User;
import com.example.bike_rental.repository.BikeRepository;
import com.example.bike_rental.repository.RentalRepository;
import com.example.bike_rental.repository.UserRepository;
import com.example.bike_rental.service.BikeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BikeControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        // czyszczenie bazy przed kazdym testem
        rentalRepository.deleteAll();
        bikeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addBikeTest() throws Exception {
        // Przygotowanie żądania
        BikeRequest bikeRequest = new BikeRequest();
        bikeRequest.setModel("Model1");
        bikeRequest.setType("GÓRSKI");
        bikeRequest.setProductionYear(2023);
        bikeRequest.setRentalPrice(80);

        // Wykonanie testu
        mockMvc.perform(post("/api/bikes/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bikeRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Rower został dodany!"));

        // Weryfikacja w bazie danych
        List<Bike> bikes = bikeRepository.findAll();
        assertEquals(1, bikes.size());
        assertEquals("Model1", bikes.get(0).getModel());
    }

    @Test
    void getAllBikesTest() throws Exception {
        // Dodanie danych do bazy
        Bike bike1 = new Bike();
        bike1.setModel("Model1");
        bike1.setType(BikeType.GÓRSKI);
        bike1.setProductionYear(2021);
        bike1.setRentalPrice(30.0);
        bikeRepository.save(bike1);

        Bike bike2 = new Bike();
        bike2.setModel("Model2");
        bike2.setType(BikeType.MIEJSKI);
        bike2.setProductionYear(2022);
        bike2.setRentalPrice(40.0);
        bikeRepository.save(bike2);

        // Wykonanie testu
        mockMvc.perform(get("/api/bikes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].model").value("Model1"))
                .andExpect(jsonPath("$[1].rentalPrice").value(40.0));
    }

    @Test
    void deleteBikeTest() throws Exception {
        // Dodanie danych do bazy
        Bike bike = new Bike();
        bike.setModel("Model1");
        bike.setType(BikeType.GÓRSKI);
        bike.setProductionYear(2021);
        bike.setRentalPrice(30.0);
        bikeRepository.save(bike);

        // Wykonanie testu
        mockMvc.perform(delete("/api/bikes/delete/{id}", bike.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Rower został usunięty!"));

        // Weryfikacja w bazie danych
        assertFalse(bikeRepository.findById(bike.getId()).isPresent());
    }

    @Test
    void deleteBikeWithRentalTest() throws Exception {
        // Dodanie danych do bazy
        Bike bike = new Bike();
        bike.setModel("Model1");
        bike.setType(BikeType.GÓRSKI);
        bike.setProductionYear(2021);
        bike.setRentalPrice(30.0);

        bike = bikeRepository.save(bike);

        // Tworzenie obiektu usera
        User user = new User();
        user.setUsername("Client1");
        user.setEmail("client1@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");
        user.setRole("ROLE_USER");
        userRepository.save(user);

        //tworzenie obiektu wypożyczenia
        Rental rental = new Rental();
        rental.setBike(bike);
        rental.setUser(user);
        rental.setStartDateTime(LocalDateTime.parse("2023-01-01T00:00:00"));
        rental.setEndDateTime(LocalDateTime.parse("2023-01-05T00:00:00"));

        rentalRepository.save(rental);

        // Wykonanie testu
        mockMvc.perform(delete("/api/bikes/delete/{id}", bike.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Nie można usunąć roweru, ponieważ ma wypożyczenia."));

        // Weryfikacja w bazie danych
        assertTrue(bikeRepository.findById(bike.getId()).isPresent());
    }

    @Test
    void editBikeTest() throws Exception {
        // Dodanie danych do bazy
        Bike bike = new Bike();
        bike.setModel("Model1");
        bike.setType(BikeType.GÓRSKI);
        bike.setProductionYear(2021);
        bike.setRentalPrice(30.0);

        bike = bikeRepository.save(bike);

        // Przygotowanie nowego żądania
        Bike bikeDetails = new Bike();
        bikeDetails.setModel("UpdatedModel");
        bikeDetails.setType(BikeType.MIEJSKI);
        bikeDetails.setProductionYear(2023);
        bikeDetails.setRentalPrice(35.0);

        // Wykonanie testu
        mockMvc.perform(put("/api/bikes/edit/{id}", bike.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bikeDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("Rower został zaktualizowany."));

        // Weryfikacja w bazie danych
        Bike updatedBike = bikeRepository.findById(bike.getId()).orElseThrow();
        assertEquals("UpdatedModel", updatedBike.getModel());
        assertEquals(BikeType.MIEJSKI, updatedBike.getType());
        assertEquals(2023, updatedBike.getProductionYear());
        assertEquals(35.0, updatedBike.getRentalPrice());
    }
}