package ru.yandex.practicum.filmorate.repository.friend;

import java.util.List;

public interface FriendRepository {
    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<Long> getFriendsIds(long id);

    List<Long> getCommonFriendsIds(long userId, long otherId);
}
