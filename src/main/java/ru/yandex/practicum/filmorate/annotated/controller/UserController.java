package ru.yandex.practicum.filmorate.annotated.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotated.model.User;

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
        log.debug("Сохранен пользователь: {}", user);
        log.debug("Хранилище пользователей теперь в состоянии: {}", users);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Метод updateUser. В теле запроса пользователь: {}", user);
        final int id = user.getId();
        final User savedUser = users.get(id);
        if (savedUser == null) {
            log.debug("Пользователь с id={} не найден", id);
            return null;
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(id, user);
        log.debug("Обновлен пользователь: {}", user);
        log.debug("Хранилище пользователей теперь в состоянии: {}", users);
        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.debug("Метод getAllUsers");
        return users.values();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleValidationExceptions(MethodArgumentNotValidException exception) {
        log.error("Ошибка валидации пользователя: {}", exception.getMessage());
    }
}
