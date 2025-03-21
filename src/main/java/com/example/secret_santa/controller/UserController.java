package com.example.secret_santa.controller;


import com.example.secret_santa.dto.UserDTO;
import com.example.secret_santa.entity.User;
import com.example.secret_santa.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint to create a new user using the DTO
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO savedUserDTO = userService.addUser(userDTO);
        return ResponseEntity.ok(savedUserDTO);
    }

    // Endpoint to delete a user by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}