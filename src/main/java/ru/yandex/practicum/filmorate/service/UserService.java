package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User saveUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            final String login = user.getLogin();
            user.setName(login);
            log.info("У пользователя не указано имя. Установили логин: {} в качестве имени.", login);
        }
        return userStorage.saveUser(user);
    }

    public User updateUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            final String login = user.getLogin();
            user.setName(user.getLogin());
            log.info("У пользователя не указано имя. Установили логин: {} в качестве имени.", login);
        }
        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(long userId, long friendId) {
        final User user = userStorage.getUserById(userId);
        user.getFriendsIds().add(friendId);
        log.info("Добавили друга пользователю {}", user);
        final User friend = userStorage.getUserById(friendId);
        friend.getFriendsIds().add(userId);
        log.info("Добавили друга пользователю {}", friend);
    }

    public void removeFriend(long userId, long friendId) {
        final User user = userStorage.getUserById(userId);
        user.getFriendsIds().remove(friendId);
        log.info("Удалили друга у пользователя {}", user);
        final User friend = userStorage.getUserById(friendId);
        friend.getFriendsIds().remove(userId);
        log.info("Удалили друга у пользователя {}", friend);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        final Set<Long> userFriendsIds = userStorage.getUserById(userId).getFriendsIds();
        log.info("ID друзей первого пользователя {}", userFriendsIds);
        final Set<Long> otherFriendsIds = userStorage.getUserById(otherId).getFriendsIds();
        log.info("ID друзей второго пользователя {}", userFriendsIds);
        return userFriendsIds.stream()
                .filter(otherFriendsIds::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public List<User> getFriends(long id) {
        return userStorage.getFriends(id);
    }
}
