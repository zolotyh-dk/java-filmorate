package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User saveUser(@Valid @RequestBody User user) {
        log.debug("Метод UserController.saveUser. В теле запроса пользователь: {}", user);
        return userService.saveUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Метод UserController.updateUser. В теле запроса пользователь: {}", user);
        return userService.updateUser(user);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.debug("Метод UserController.getAllUsers");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
