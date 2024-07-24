package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserRepository {
    User saveUser(User user);

    User updateUser(User user);

    Collection<User> getAllUsers();

    User getUserById(long id);

    List<User> getFriends(long id);
}
