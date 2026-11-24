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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SecurityRulesTest {

    @Autowired private MockMvc mockMvc;
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
    void myReviews_without_Token_returns401() throws Exception{
        mockMvc.perform(get("/reviews/my-reviews"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void booksPublicEndpoint_without_Token_returns200() throws Exception{
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_withValidToken_returns200() throws Exception{
        String token = jwtService.generateToken("testuser");

        mockMvc.perform(get("/reviews/my-reviews")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void tamperedToken_returns401() throws Exception{
        String token = jwtService.generateToken("testuser");

        String[] parts = token.split("\\.");
        String signature = parts[2];
        char swapped = signature.charAt(0) == 'A' ? 'B' : 'A';
        String tamperedToken = parts[0] + "." + parts[1] + "." + swapped + signature.substring(1);

        mockMvc.perform(get("/reviews/my-reviews")
                .header("Authorization", "Bearer " + tamperedToken))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void expiredToken_returns401() throws Exception{
        String token = jwtService.generateToken("testuser");

        Thread.sleep(6000);

        mockMvc.perform(get("/reviews/my-reviews")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usersAdminOnlyEndpoint_withUserToken_shouldReturn403() throws Exception{
        String token = jwtService.generateToken("testuser");

        mockMvc.perform(get("/users")
                .header("Authorization","Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_withAdminToken_returns200() throws Exception{
        String token = jwtService.generateToken("testadmin");

        mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpoint_withNoToken_returns401() throws Exception{
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }
}
