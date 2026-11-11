package com.jemshit.book_api.mapper;

import com.jemshit.book_api.Role;
import com.jemshit.book_api.dto.MeResponseDTO;
import com.jemshit.book_api.dto.UserRequestDTO;
import com.jemshit.book_api.dto.UserResponseDTO;
import com.jemshit.book_api.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO convertToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername()
        );
    }

    public User convertToUserEntity(UserRequestDTO userDTO) {

        String hashedPassword = passwordEncoder.encode(userDTO.password());

        User user = new User();

        user.setPassword(hashedPassword);
        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        user.setRole(Role.USER);

        return user;
    }

    public MeResponseDTO convertToMeResponseDTO(User user){
        return new MeResponseDTO(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole());
    }
}
