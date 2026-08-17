package com.placementiq.backend.controller;

import com.placementiq.backend.model.User;
import com.placementiq.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }
    @PostMapping("/login")
    public User loginUser(@RequestBody User user) {

    return userService.loginUser(user.getEmail(), user.getPassword());
    }
}