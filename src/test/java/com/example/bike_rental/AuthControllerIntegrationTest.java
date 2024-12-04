package com.example.bike_rental;

import com.example.bike_rental.model.User;
import com.example.bike_rental.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        // czyszczenie bazy przed kazdym testem
        userRepository.deleteAll();
    }

    @Test
    void registerUserSuccessfully() throws Exception {
        // Przygotowanie żądania
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("testuser");
        request.setEmail("testuser@example.com");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        // Serializacja żądania do JSON
        String requestBody = objectMapper.writeValueAsString(request);

        // Wysłanie żądania POST do endpointa rejestracji
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        // Weryfikacja w bazie danych
        User savedUser = userRepository.findByUsername("testuser").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("testuser@example.com");
        assertThat(savedUser.getPassword()).isNotEqualTo("password123"); // Powinno być zakodowane
    }

    @Test
    void registerUserPasswordsDoNotMatch() throws Exception {
        // Przygotowanie żądania z błędnymi hasłami
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("testuser");
        request.setEmail("testuser@example.com");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPassword("password123");
        request.setConfirmPassword("password12345");

        // Serializacja żądania do JSON
        String requestBody = objectMapper.writeValueAsString(request);

        // Wysłanie żądania POST do endpointa rejestracji
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Passwords do not match"));

        // Weryfikacja, że użytkownik nie został zapisany w bazie danych
        assertThat(userRepository.findByUsername("testuser")).isEmpty();
    }

    @Test
    void registerUserAlreadyExists() throws Exception {
        // Zapisanie użytkownika o tej samej nazwie przed testem
        User existingUser = new User();
        existingUser.setUsername("testuser");
        existingUser.setEmail("existing@example.com");
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        existingUser.setPassword("password123"); // Zakodowane hasło
        userRepository.save(existingUser);

        // Przygotowanie żądania rejestracji
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("testuser");
        request.setEmail("testuser@example.com");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        // Serializacja żądania do JSON
        String requestBody = objectMapper.writeValueAsString(request);

        // Wysłanie żądania POST do endpointa rejestracji
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("User already exists"));

        // Weryfikacja, że użytkownik nie został nadpisany
        User savedUser = userRepository.findByUsername("testuser").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("existing@example.com"); // Pozostał niezmieniony
    }


    @Test
    void loginSuccessfull() throws Exception{
        //usuniecie danych
        userRepository.deleteAll();

        // Dodanie użytkownika testowego
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("password123")); // Hasło zakodowane
        user.setRole("ROLE_USER");
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        // Serializacja obiektu do JSON
        String requestBody = objectMapper.writeValueAsString(request);

        // Wysłanie żądania POST do endpointa logowania
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void loginNotSuccessfull() throws Exception{
        //usuniecie danych
        userRepository.deleteAll();

        // Dodanie użytkownika testowego
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("password123")); // Hasło zakodowane
        user.setRole("ROLE_USER");
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpasswd");

        // Serializacja obiektu do JSON
        String requestBody = objectMapper.writeValueAsString(request);

        // Wysłanie żądania POST do endpointa logowania
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isForbidden());
    }

}
