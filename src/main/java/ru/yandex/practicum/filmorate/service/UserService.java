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
        log.debug("Метод UserService.saveUser. Сохраняем пользователя={}", user);
        return userStorage.saveUser(user);
    }

    public User updateUser(User user) {
        log.debug("Метод UserService.updateUser. Обновляем пользователя={}", user);
        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        log.debug("Метод UserService.getAllUsers. Получаем всех пользователей");
        return userStorage.getAllUsers();
    }

    public void addFriend(long userId, long friendId) {
        log.debug("Метод UserService.addFriend. Пользователь с id={} добавляет друга с id={}", userId, friendId);
        User user = userStorage.getUserById(userId);
        user.getFriendsIds().add(friendId);

        User friend = userStorage.getUserById(friendId);
        friend.getFriendsIds().add(userId);
    }

    public void removeFriend(long userId, long friendId) {
        log.debug("Метод UserService.removeFriend. Пользователь с id={} удаляет друга с id={}", userId, friendId);
        User user = userStorage.getUserById(userId);
        user.getFriendsIds().remove(friendId);

        User friend = userStorage.getUserById(friendId);
        friend.getFriendsIds().remove(userId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        log.debug("Метод UserService.getCommonFriends. Получаем общих друзей пользователей с id={} и с id={}", userId, otherId);
        User user = userStorage.getUserById(userId);
        Set<Long> userFriendsIds = user.getFriendsIds();

        User other = userStorage.getUserById(otherId);
        Set<Long> otherFriendsIds = other.getFriendsIds();

        return userFriendsIds.stream()
                .filter(otherFriendsIds::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public User getUserById(long id) {
        log.debug("Метод UserService.getUserById. Получаем пользователя по id={}", id);
        return userStorage.getUserById(id);
    }

    public List<User> getFriends(long id) {
        log.debug("Метод UserService.getFriends. Получаем друзей пользователя с id={}", id);
        return userStorage.getFriends(id);
    }
}
