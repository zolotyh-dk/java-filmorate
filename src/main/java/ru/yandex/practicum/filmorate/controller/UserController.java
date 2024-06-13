package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int generatorId;
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User saveUser(@Valid @RequestBody User user) {
        log.debug("Метод saveUser. В теле запроса пользователь: {}", user);
        final int id = ++generatorId;
        user.setId(id);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(id, user);
        log.debug("Сохранен пользователь: {}\nХранилище пользователей теперь в состоянии: {}", user, users);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Метод updateUser. В теле запроса пользователь: {}", user);
        final int id = user.getId();
        final User savedUser = users.get(id);
        if (savedUser == null) {
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(id, user);
        log.debug("Обновлен пользователь: {}\nХранилище пользователей теперь в состоянии: {}", user, users);
        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.debug("Метод getAllUsers");
        Collection<User> allUsers = users.values();
        log.debug("Возвращаем коллекцию пользователей: {}", allUsers);
        return allUsers;
    }
}
