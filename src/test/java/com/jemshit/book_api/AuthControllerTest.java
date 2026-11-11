package com.jemshit.book_api;

import com.jemshit.book_api.model.User;
import com.jemshit.book_api.repository.UserRepository;
import com.jemshit.book_api.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.hamcrest.Matchers.matchesPattern;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JWTService jwtService;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();

        userRepository.save(new User("testuser",
                passwordEncoder.encode("password123"), "testuser@test.com", Role.USER));
        userRepository.save(new User("testadmin",
                passwordEncoder.encode("password456"), "testadmin@test.com", Role.ADMIN));

    }

    @Test
    void loginEndpoint_withWrongCredentials_returns401() throws Exception{

        String token = jwtService.generateToken("testuser");

        String jsonBody = """
                {
                    "username": "testuser",
                    "password": "password111"
                }
                """;

        mockMvc.perform(post("/login")
                        .header("Authentication", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginEndpoint_withValidCredentials_returns200AndCreatesJWT() throws Exception{

        String token = jwtService.generateToken("testuser");

        String jsonBody = """
                {
                    "username": "testuser",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/login")
                        .header("Authentication", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string(matchesPattern("^[\\w-]+\\.[\\w-]+\\.[\\w-]+$")));
    }
}
