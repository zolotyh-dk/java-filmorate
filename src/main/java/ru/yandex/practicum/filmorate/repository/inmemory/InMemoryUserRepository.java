package ru.yandex.practicum.filmorate.repository.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserRepository implements UserRepository {
    private int generatorId;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User saveUser(User user) {
        final long id = ++generatorId;
        user.setId(id);
        log.info("Фильму присвоен ID: {}", id);
        users.put(id, user);
        log.info("Пользователь сохранен в хранилище: {}", users);
        return user;
    }

    @Override
    public User updateUser(User user) {
        final long id = user.getId();
        final User savedUser = getUserById(id);
        user.setFriendsIds(savedUser.getFriendsIds());
        users.put(id, user);
        log.debug("Обновлен пользователь в хранилище: {}", users);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(long id) {
        final User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID: " + id + " не найден.");
        }
        return user;
    }

    @Override
    public List<User> getFriends(long id) {
        return getUserById(id).getFriendsIds().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }
}
