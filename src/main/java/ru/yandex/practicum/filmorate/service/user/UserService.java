package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    User saveUser(User user);

    User updateUser(User user);

    Collection<User> getAllUsers();

    User getUserById(long id);
}
