package com.example.bike_rental;


import com.example.bike_rental.model.User;

import com.example.bike_rental.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTests {

        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;
        @MockBean
        private UserRepository userRepository;
        @MockBean
        private org.springframework.security.authentication.AuthenticationManager authenticationManager;
        @MockBean
        private PasswordEncoder passwordEncoder;
        @MockBean
        private JwtService jwtService;

        @Test
        void registrationTest() throws Exception {
                RegistrationRequest request = new RegistrationRequest();
                request.setUsername("testuser");
                request.setEmail("testuser@example.com");
                request.setFirstName("Test");
                request.setLastName("User");
                request.setPassword("password123");
                request.setConfirmPassword("password123");

                // Serializacja obiektu do JSON
                String requestBody = objectMapper.writeValueAsString(request);

                // Wysłanie żądania i weryfikacja wyniku
                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Użytkownik pomyslnie zarejestrowany"));

                // Weryfikacja, czy użytkownik został zapisany w repozytorium
                Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        }

        @Test
        void loginTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("testuser");
                request.setPassword("password123");

                // Zamockowanie autentykacji
                Authentication authentication = Mockito.mock(Authentication.class);
                Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                                .thenReturn(authentication);

                // Zamockowanie generowania tokenu
                String mockToken = "mockJwtToken";
                Mockito.when(jwtService.generateToken(Mockito.anyString(), Mockito.anyList())).thenReturn(mockToken);

                // Serializacja obiektu do JSON
                String requestBody = objectMapper.writeValueAsString(request);

                // Wysłanie żądania i weryfikacja wyniku
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").value(mockToken));

        }


        @Test
        void registrationTestWrongPassword() throws Exception {
                RegistrationRequest request = new RegistrationRequest();
                request.setUsername("testuser");
                request.setEmail("testuser@example.com");
                request.setFirstName("Test");
                request.setLastName("User");
                request.setPassword("password123");
                request.setConfirmPassword("password12345");

                
                // Serializacja obiektu do JSON
                String requestBody = objectMapper.writeValueAsString(request);

                // Wysłanie żądania i weryfikacja wyniku
                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Hasła się nie zgadzają!"));

        }

}
