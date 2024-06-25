package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserStorage userStorage;

    @PostMapping
    public User saveUser(@Valid @RequestBody User user) {
        log.debug("Метод UserController.saveUser. В теле запроса пользователь: {}", user);
        return userStorage.saveUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Метод UserController.updateUser. В теле запроса пользователь: {}", user);
        return userStorage.updateUser(user);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.debug("Метод UserController.getAllUsers");
        return userStorage.getAllUsers();
    }
}
