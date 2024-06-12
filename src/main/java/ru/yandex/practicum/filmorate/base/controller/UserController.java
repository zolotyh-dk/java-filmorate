package ru.yandex.practicum.filmorate.base.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.base.exception.ValidationException;
import ru.yandex.practicum.filmorate.base.model.User;

import java.time.LocalDate;
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
    public User saveUser(@RequestBody User user) throws ValidationException {
        log.debug("Метод saveUser. В теле запроса пользователь: {}", user);

        try {
            validateUser(user);
        } catch (ValidationException e) {
            log.error("Ошибка валидации пользователя: {}", e.getMessage());
            throw e;
        }

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
    public User updateUser(@RequestBody User user) throws ValidationException {
        log.debug("Метод updateUser. В теле запроса пользователь: {}", user);

        try {
            validateUser(user);
        } catch (ValidationException e) {
            log.error("Ошибка валидации пользователя: {}", e.getMessage());
            throw e;
        }

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

    private void validateUser(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getEmail().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
