package com.jemshit.book_api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReviewRequestDTO (@NotBlank String reviewText,
                                @Min(1) @Max(5)double rating,
                                @NotBlank Long bookId,
                                @NotBlank Long userId) {
}
