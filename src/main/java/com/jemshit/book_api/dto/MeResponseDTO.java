package com.jemshit.book_api.dto;

import com.jemshit.book_api.Role;

public record MeResponseDTO(Long id, String username, String email, Role role) {
}
