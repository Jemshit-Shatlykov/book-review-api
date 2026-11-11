package com.jemshit.book_api.mapper;

import com.jemshit.book_api.dto.ReviewRequestDTO;
import com.jemshit.book_api.dto.ReviewResponseDTO;
import com.jemshit.book_api.exception.BookNotFoundException;
import com.jemshit.book_api.exception.UserNotFoundException;
import com.jemshit.book_api.model.Book;
import com.jemshit.book_api.model.Review;
import com.jemshit.book_api.model.User;
import com.jemshit.book_api.repository.BookRepository;
import com.jemshit.book_api.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public  ReviewMapper(BookMapper bookMapper, UserMapper userMapper,
                         BookRepository bookRepository, UserRepository userRepository) {
        this.bookMapper = bookMapper;
        this.userMapper = userMapper;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public ReviewResponseDTO convertToResponseDTO(Review review){
        return new ReviewResponseDTO(
                review.getId(),
                review.getReviewText(),
                review.getRating(),
                bookMapper.convertToResponseDTO(review.getBook()),
                userMapper.convertToResponseDTO(review.getUser())
        );
    }

    public Review convertToReviewEntity(ReviewRequestDTO dto){

        Book book = bookRepository.findById(dto.bookId())
                .orElseThrow(() -> new BookNotFoundException("Book with id " + dto.bookId() + " not found"));
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException("User with id " + dto.userId() + " not found"));

        Review review = new Review();

        review.setReviewText(dto.reviewText());
        review.setRating(dto.rating());
        review.setBook(book);
        review.setUser(user);

        return review;
    }
}
