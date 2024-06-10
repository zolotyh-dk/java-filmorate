package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {
    @PostMapping
    public User createUser(@RequestBody User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return null;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return null;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return new ArrayList<>();
    }
}
