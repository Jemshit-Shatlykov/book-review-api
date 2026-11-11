package com.jemshit.book_api.dto;

import jakarta.validation.constraints.NotBlank;

public record BookRequestDTO (@NotBlank String title, @NotBlank String author, @NotBlank String isbn) {}
