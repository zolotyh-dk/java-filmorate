package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User saveUser(User user) {
        return userStorage.saveUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        user.getFriendsIds().add(friendId);

        User friend = userStorage.getUserById(friendId);
        friend.getFriendsIds().add(userId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        user.getFriendsIds().remove(friendId);

        User friend = userStorage.getUserById(friendId);
        friend.getFriendsIds().remove(userId);
    }

    public Set<Long> getCommonFriends(long userId, long otherId) {
        User user = userStorage.getUserById(userId);
        Set<Long> userFriendsIds = user.getFriendsIds();

        User other = userStorage.getUserById(otherId);
        Set<Long> otherFriendsIds = other.getFriendsIds();

        return userFriendsIds.stream()
                .filter(otherFriendsIds::contains)
                .collect(Collectors.toSet());
    }
}
