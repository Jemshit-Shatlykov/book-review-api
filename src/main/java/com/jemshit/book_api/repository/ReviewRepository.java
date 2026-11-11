package com.jemshit.book_api.repository;

import com.jemshit.book_api.model.Book;
import com.jemshit.book_api.model.Review;
import com.jemshit.book_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByBookAndUser(Book book, User user);
}
