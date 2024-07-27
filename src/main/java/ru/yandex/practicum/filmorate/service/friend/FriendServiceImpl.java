package ru.yandex.practicum.filmorate.service.friend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FriendRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Override
    public void addFriend(long userId, long friendId) {
        final User user = userRepository.getUserById(userId);
        final User friend = userRepository.getUserById(friendId);
        log.debug("Проверили, что пользователь с ID: {} и друг c ID: {} существуют в базе данных", userId, friendId);
        friendRepository.addFriend(userId, friendId);
        log.info("Добавили друга пользователю {}", user);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        final User user = userRepository.getUserById(userId);
        final User friend = userRepository.getUserById(friendId);
        log.debug("Проверили, что пользователь с ID: {} и друг c ID: {} существуют в базе данных", userId, friendId);
        friendRepository.removeFriend(userId, friendId);
        log.info("Удалили друга у пользователя {}", user);
    }

    @Override
    public List<User> getFriends(long id) {
        final User user = userRepository.getUserById(id);
        log.debug("Проверили, что пользователь с ID: {} существует в базе данных", id);
        final List<Integer> friendIds = friendRepository.getFriendsIds(id);
        log.debug("Получили из БД список ID друзей пользователя: {}", friendIds);
        return userRepository.getUsersByIds(friendIds);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        final User user = userRepository.getUserById(userId);
        final User other = userRepository.getUserById(otherId);
        log.debug("Проверили, что пользователь с ID: {} и другой c ID: {} существуют в базе данных", userId, otherId);
        final List<Integer> commonFriendsIds = friendRepository.getCommonFriendsIds(userId, otherId);
        log.debug("Получили из БД список ID общих друзей пользователей: {}", commonFriendsIds);
        return userRepository.getUsersByIds(commonFriendsIds);
    }
}
