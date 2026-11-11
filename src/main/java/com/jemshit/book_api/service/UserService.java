package com.jemshit.book_api.service;

import com.jemshit.book_api.dto.MeResponseDTO;
import com.jemshit.book_api.dto.UserRequestDTO;
import com.jemshit.book_api.dto.UserResponseDTO;
import com.jemshit.book_api.exception.DuplicateUserException;
import com.jemshit.book_api.exception.UserNotFoundException;
import com.jemshit.book_api.mapper.UserMapper;
import com.jemshit.book_api.model.User;
import com.jemshit.book_api.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import org.springframework.security.access.AccessDeniedException;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<MeResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper :: convertToMeResponseDTO)
                .toList();
    }

    public MeResponseDTO getMe(Authentication auth){
        User user = userRepository.findByUsername(auth.getName());

        if(user == null) throw new UserNotFoundException("This user doesn't exist");

        return userMapper.convertToMeResponseDTO(user);
    }

    public UserResponseDTO insertUser(UserRequestDTO userrequestDTO) {

        if(userRepository.existsByUsername(userrequestDTO.username())){
            throw new DuplicateUserException("User with username " + userrequestDTO.username() + " already exists");
        }

        return userMapper.convertToResponseDTO(userRepository.save(userMapper.convertToUserEntity(userrequestDTO)));
    }

    public UserResponseDTO getUserById(Long id){
        User user =  userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));

        return userMapper.convertToResponseDTO(user);
    }

    public UserResponseDTO updateUser(UserRequestDTO userRequestDTO, Long id, Authentication auth) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));

        boolean isOwner = existingUser.getUsername().equals(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if(!isOwner && !isAdmin){
            throw new AccessDeniedException("You can only modify your own account");
        }

        String hashedPassword = passwordEncoder.encode(userRequestDTO.password());

        existingUser.setPassword(hashedPassword);
        existingUser.setUsername(userRequestDTO.username());
        existingUser.setEmail(userRequestDTO.email());

        return userMapper.convertToResponseDTO(userRepository.save(existingUser));
    }

    public void deleteUser(Long id, Authentication auth) {
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException("User with id: " + id + "not found"));

        boolean isOwner = user.getUsername().equals(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if(!isOwner && !isAdmin){
            throw new AccessDeniedException("You can only delete your own account");
        }
        userRepository.deleteById(id);
    }
}
