package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    User saveUser(User user);

    User updateUser(User user);

    Collection<User> getAllUsers();

    User getUserById(long id);

    List<User> getFriends(long id);
}
