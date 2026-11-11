package com.jemshit.book_api.controller;

import com.jemshit.book_api.dto.ReviewRequestDTO;
import com.jemshit.book_api.dto.ReviewResponseDTO;
import com.jemshit.book_api.dto.ReviewUpdateDTO;
import com.jemshit.book_api.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<ReviewResponseDTO> getAllReviews() {
        return  reviewService.getAllReviews();
    }

    @PostMapping
    public ReviewResponseDTO addReview(@Valid @RequestBody ReviewRequestDTO dto) {
       return reviewService.insertReview(dto);
    }

    @DeleteMapping("{id}")
    public void deleteReview(@PathVariable Long id, Authentication auth) {
        reviewService.deleteReviewById(id, auth);
    }

    @GetMapping("{id}")
    public ReviewResponseDTO getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @PutMapping("{id}")
    public ReviewResponseDTO updateReview(@Valid @RequestBody ReviewUpdateDTO updateDTO, @PathVariable Long id, Authentication auth) {
       return reviewService.updateReview(updateDTO, id, auth);
    }

    @GetMapping("/by-book")
    public List<ReviewResponseDTO> getReviewsByBookId(@RequestParam Long bookId) {
        return reviewService.getReviewsByBookId(bookId);
    }

    @GetMapping("/by-user")
    public List<ReviewResponseDTO> getReviewsByUserId(@RequestParam Long userId){
        return reviewService.getReviewsByUserId(userId);
    }

    @GetMapping("/my-reviews")
    public List<ReviewResponseDTO> getMyReviews(Authentication auth){
        return reviewService.getMyReviews(auth);
    }


    @GetMapping("/average-rating")
    public double getAverageRatingOfBook(@RequestParam Long bookId) {
        return reviewService.getAverageRatingByBookId(bookId);
    }
}
