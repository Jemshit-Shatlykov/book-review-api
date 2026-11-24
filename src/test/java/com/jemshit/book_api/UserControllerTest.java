package com.jemshit.book_api;

import com.jemshit.book_api.model.User;
import com.jemshit.book_api.repository.ReviewRepository;
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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JWTService jwtService;

    @BeforeEach
    void setUp(){
        reviewRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(new User("testuser",
                passwordEncoder.encode("password123"), "testuser@test.com", Role.USER));
        userRepository.save(new User("testadmin",
                passwordEncoder.encode("password456"), "testadmin@test.com", Role.ADMIN));

    }

    @Test
    void getUsersEndpoint_withAdminToken_returnsUsers() throws Exception{
        String token = jwtService.generateToken("testadmin");

        mockMvc.perform(get("/users").
                header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].username", containsInAnyOrder("testuser", "testadmin")));
    }

    @Test
    void postRegisterUser_PublicEndpoint_returnsUsernameAndID() throws Exception{

        String jsonBody = """
                {
                    "username": "Serdar",
                    "password": "Serdar07!",
                    "email": "caryyewserdar@gmail.com"
                }
                """;

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Serdar"))
                .andExpect(jsonPath("$.password").doesNotExist());

        User saved = userRepository.findByUsername("Serdar");
        assertNotEquals("Serdar07!", saved.getPassword());
        assertTrue(passwordEncoder.matches("Serdar07!", saved.getPassword()));

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isConflict());


    }
}
