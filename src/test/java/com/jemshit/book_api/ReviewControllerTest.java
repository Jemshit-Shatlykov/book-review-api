package com.jemshit.book_api;

import com.jemshit.book_api.model.Book;
import com.jemshit.book_api.model.Review;
import com.jemshit.book_api.model.User;
import com.jemshit.book_api.repository.BookRepository;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ReviewControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JWTService jwtService;

    @BeforeEach
    void setUp(){
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        Book book1 = new Book("978-0735211292", "Atomic Habits", "James Clear");
        Book book2 = new Book("978-0132350884", "Clean Code", "Robert C. Martin");
        Book book3 = new Book("978-0596007126", "Head First Design Patterns", "Eric Freeman");

        User testuser1 = new User("testuser",
                passwordEncoder.encode("password123"), "testuser@test.com", Role.USER);
        User testuser2 = new User("testuser2",
                passwordEncoder.encode("password1234"), "testuser2@test.com", Role.USER);
        User testadmin = new User("testadmin",
                passwordEncoder.encode("password456"), "testadmin@test.com", Role.ADMIN);

        Review review1 = new Review(book1, testuser1, "Goooode", 4);
        Review review2 = new Review(book2, testuser2, "Onatmy Arslan", 3);
        Review adminReview = new Review(book3, testadmin, "Mena bolya diyan", 5);
        Review adminReview2 = new Review(book2, testadmin, "normalny", 4);

        bookRepository.saveAll(List.of(book1, book2, book3));
        userRepository.saveAll(List.of(testuser1, testuser2, testadmin));
        reviewRepository.saveAll(List.of(review1, review2, adminReview, adminReview2));
    }

    @Test
    void myReviewEndpoint_withValidToken_returnsOwnReviews() throws Exception{
        String token = jwtService.generateToken("testadmin");

        mockMvc.perform(get("/reviews/my-reviews")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].user.username", everyItem(is("testadmin"))))
                .andExpect(jsonPath("$[0].user.password").doesNotExist());
    }

    @Test
    void getReviews_PublicEndpoint_returnsAllReviews() throws Exception{

        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].user.password").doesNotExist());
    }

    @Test
    void deleting_otherReviews_returnsForbidden() throws Exception {
        String token = jwtService.generateToken("testuser");

        Review reviewOwnedByTestuser2 = reviewRepository.findAll().stream()
                .filter(review -> review.getUser().getUsername().equals("testuser2"))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(delete("/reviews/{id}", reviewOwnedByTestuser2.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
