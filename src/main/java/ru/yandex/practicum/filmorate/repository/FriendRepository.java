package ru.yandex.practicum.filmorate.repository;

import java.util.List;

public interface FriendRepository {
    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<Integer> getFriendsIds(long id);

    List<Integer> getCommonFriendsIds(long userId, long otherId);
}
