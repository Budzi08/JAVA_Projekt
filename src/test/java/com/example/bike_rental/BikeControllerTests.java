package com.example.bike_rental;

import com.example.bike_rental.controller.BikeController;
import com.example.bike_rental.dto.BikeRequest;
import com.example.bike_rental.dto.BikeResponse;
import com.example.bike_rental.model.Bike;
import com.example.bike_rental.model.BikeType;
import com.example.bike_rental.repository.BikeRepository;
import com.example.bike_rental.repository.RentalRepository;

import com.example.bike_rental.service.BikeService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BikeControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BikeService bikeService;
    @MockBean
    private BikeRepository bikeRepository;
    @MockBean
    private RentalRepository rentalRepository;

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
    }

    @Test
    void getAllBikesTest() throws Exception {
        List<BikeResponse> bikes = List.of(
                new BikeResponse("Model1", "GÓRSKI", 2021, 30.0),
                new BikeResponse("Model2", "MIEJSKI", 2022, 40.0));
        Mockito.when(bikeService.getAllBikes()).thenReturn(bikes);

        // Wykonanie testu
        mockMvc.perform(get("/api/bikes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].model").value("Model1"))
                .andExpect(jsonPath("$[1].rentalPrice").value(40.0));
    }

    @Test
    void deleteBikeTest() throws Exception {
        Long bikeId = 1L;
        Bike bike = new Bike();
        bike.setId(bikeId);

        Mockito.when(bikeRepository.findById(bikeId)).thenReturn(java.util.Optional.of(bike));
        Mockito.when(rentalRepository.existsByBike(bike)).thenReturn(false); // Brak wypożyczenia

        // Wykonanie testu
        mockMvc.perform(delete("/api/bikes/delete/{id}", bikeId))
                .andExpect(status().isOk())
                .andExpect(content().string("Rower został usunięty!"));
    }

    @Test
    void deleteBikeWithRentalTest() throws Exception {
        Long bikeId = 1L;
        Bike bike = new Bike();
        bike.setId(bikeId);

        //symulacja zachowania repozytorium
        Mockito.when(bikeRepository.findById(bikeId)).thenReturn(java.util.Optional.of(bike));
        Mockito.when(rentalRepository.existsByBike(bike)).thenReturn(true); // rower wypożyczony

        // Wykonanie testu
        mockMvc.perform(delete("/api/bikes/delete/{id}", bikeId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Nie można usunąć roweru, ponieważ ma wypożyczenia."));
    }

    @Test
    void editBikeTest() throws Exception {
        Long bikeId = 1L;
        Bike bikeDetails = new Bike();
        bikeDetails.setModel("UpdatedModel");
        bikeDetails.setType(BikeType.MIEJSKI);
        bikeDetails.setProductionYear(2023);
        bikeDetails.setRentalPrice(35.0);

        Mockito.when(bikeRepository.findById(bikeId)).thenReturn(java.util.Optional.of(new Bike()));

        // Wykonanie testu
        mockMvc.perform(put("/api/bikes/edit/{id}", bikeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("Rower został zaktualizowany."));
    }
}
