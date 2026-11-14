package com.jemshit.book_api.service;

import com.jemshit.book_api.dto.ReviewRequestDTO;
import com.jemshit.book_api.dto.ReviewResponseDTO;
import com.jemshit.book_api.dto.ReviewUpdateDTO;
import com.jemshit.book_api.exception.*;

import com.jemshit.book_api.mapper.ReviewMapper;
import com.jemshit.book_api.model.Book;
import com.jemshit.book_api.model.Review;
import com.jemshit.book_api.model.User;
import com.jemshit.book_api.repository.BookRepository;
import com.jemshit.book_api.repository.ReviewRepository;
import com.jemshit.book_api.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import org.springframework.security.access.AccessDeniedException;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,  BookRepository bookRepository,
                         UserRepository userRepository,  ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.reviewMapper = reviewMapper;
    }

    public List<ReviewResponseDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(reviewMapper :: convertToResponseDTO)
                .toList();
    }

    public ReviewResponseDTO insertReview(ReviewRequestDTO dto) {

        Book book = bookRepository.findById(dto.bookId())
                .orElseThrow(() -> new BookNotFoundException("Book with id " + dto.bookId() + " not found"));

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException("User with id " + dto.userId() + " not found"));


        if(reviewRepository.existsByBookAndUser(book, user)) {
            throw new DuplicateReviewException(user.getUsername() + " have already reviewed " +  book.getTitle());
        }

        return reviewMapper.convertToResponseDTO(reviewRepository.save(reviewMapper.convertToReviewEntity(dto)));
    }

    public void deleteReviewById(Long id, Authentication auth) {
        Review review = reviewRepository.findById(id)
                        .orElseThrow(() -> new ReviewNotFoundException("Review with id: " + id + " not found"));

        boolean isOwner = review.getUser().getUsername().equals(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if(!isOwner && !isAdmin){
            throw new AccessDeniedException("You can only delete your own reviews");
        }

        reviewRepository.deleteById(id);
    }

    public ReviewResponseDTO getReviewById(Long id) {
        Review review =  reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review with id " + id + " not found"));

        return reviewMapper.convertToResponseDTO(review);
    }

    public ReviewResponseDTO updateReview(ReviewUpdateDTO updateDTO, Long id, Authentication auth) {

        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review with id " + id + " not found"));

        boolean isOwner = existingReview.getUser().getUsername().equals(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if(!isOwner && !isAdmin){
            throw new AccessDeniedException("You can only delete your own reviews");
        }

        existingReview.setRating(updateDTO.rating());
        existingReview.setReviewText(updateDTO.reviewText());

        return reviewMapper.convertToResponseDTO(reviewRepository.save(existingReview));
    }

    public List<ReviewResponseDTO> getReviewsByBookId(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));

        return reviewRepository.findAll().stream()
                .filter(r -> r.getBook() == book)
                .map(reviewMapper :: convertToResponseDTO)
                .toList();
    }

    public List<ReviewResponseDTO> getReviewsByUserId(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));

        return reviewRepository.findAll().stream()
                .filter(r -> r.getUser() == user)
                .map(reviewMapper :: convertToResponseDTO)
                .toList();
    }

    public List<ReviewResponseDTO> getMyReviews(Authentication auth){
        String username = auth.getName();

        User user = userRepository.findByUsername(username);

        if(user == null) throw new UserNotFoundException("User not found");

        return getReviewsByUserId(user.getId());
    }

    public double getAverageRatingByBookId(Long id) {
        List<ReviewResponseDTO> reviews = getReviewsByBookId(id);
        if (reviews.isEmpty()) return 0.0;

        double ratingSum = reviews.stream()
                .mapToDouble(ReviewResponseDTO :: rating)
                .sum();

        return ratingSum / reviews.size();
    }
}
