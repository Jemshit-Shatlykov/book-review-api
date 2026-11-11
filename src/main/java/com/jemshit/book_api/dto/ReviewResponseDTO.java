package com.jemshit.book_api.dto;

public record ReviewResponseDTO(Long id, String reviewText, double rating, BookResponseDTO book, UserResponseDTO user) {
}
