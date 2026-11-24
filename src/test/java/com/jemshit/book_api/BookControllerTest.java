package com.jemshit.book_api;

import com.jemshit.book_api.model.Book;
import com.jemshit.book_api.model.User;
import com.jemshit.book_api.repository.BookRepository;
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


import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private BookRepository bookRepository;
    @Autowired private JWTService jwtService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp(){
        bookRepository.deleteAll();
        userRepository.deleteAll();

        bookRepository.save(new Book("978-0735211292", "Atomic Habits", "James Clear"));
        bookRepository.save(new Book("978-0132350884", "Clean Code", "Robert C. Martin"));
        bookRepository.save(new Book("978-0596007126", "Head First Design Patterns", "Eric Freeman"));

        userRepository.save(new User("testuser",
                passwordEncoder.encode("password123"), "testuser@test.com", Role.USER));
        userRepository.save(new User("testadmin",
                passwordEncoder.encode("password456"), "testadmin@test.com", Role.ADMIN));
    }

    @Test
    void getBooks_publicEndpoint_returnsBooks() throws Exception{
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Atomic Habits", "Clean Code", "Head First Design Patterns")));
    }

    @Test
    void deleteEndpoint_withUserToken_returns403WithNoBody() throws Exception{
        String token = jwtService.generateToken("testuser");

        mockMvc.perform(delete("/books/1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void postEndpoint_withUserToken_returns403WithNoBody() throws Exception{
        String token = jwtService.generateToken("testuser");

        mockMvc.perform(post("/books")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void putEndpoint_withUserToken_returns403WithNoBody() throws Exception{
        String token = jwtService.generateToken("testuser");

        mockMvc.perform(put("/books/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
