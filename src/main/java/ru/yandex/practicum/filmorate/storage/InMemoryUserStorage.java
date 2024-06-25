package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int generatorId;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User saveUser(User user) {
        final int id = ++generatorId;
        user.setId(id);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(id, user);
        log.debug("Сохранен пользователь: {}\nХранилище пользователей теперь в состоянии: {}", user, users);
        return user;
    }

    @Override
    public User updateUser(User user) {
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

    @Override
    public Collection<User> getAllUsers() {
        Collection<User> allUsers = users.values();
        log.debug("Возвращаем коллекцию пользователей: {}", allUsers);
        return allUsers;
    }
}
