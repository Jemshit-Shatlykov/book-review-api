package com.jemshit.book_api.controller;

import com.jemshit.book_api.dto.MeResponseDTO;
import com.jemshit.book_api.dto.UserRequestDTO;
import com.jemshit.book_api.dto.UserResponseDTO;
import com.jemshit.book_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<MeResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/me")
    public MeResponseDTO getMe(Authentication auth){
        return userService.getMe(auth);
    }

    @PostMapping("register")
    public UserResponseDTO registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return userService.insertUser(userRequestDTO);
    }

    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@Valid @RequestBody UserRequestDTO userRequestDTO, @PathVariable Long id, Authentication auth) {
        return userService.updateUser(userRequestDTO, id, auth);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id, Authentication auth) {
        userService.deleteUser(id, auth);
    }
}
