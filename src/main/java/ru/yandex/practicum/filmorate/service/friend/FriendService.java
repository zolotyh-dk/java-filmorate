package ru.yandex.practicum.filmorate.service.friend;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendService {
    void addFriend(long id, long friendId);

    void removeFriend(long id, long friendId);

    List<User> getFriends(long id);

    List<User> getCommonFriends(long id, long otherId);
}
