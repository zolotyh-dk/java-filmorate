package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        user.getFriendsIds().add(friendId);

        User friend = userStorage.getUserById(friendId);
        friend.getFriendsIds().add(userId);
    }
}
