package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.repository.friend.JdbcFriendRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(JdbcFriendRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcFriendRepositoryTest {

    private final JdbcFriendRepository friendRepository;

    @BeforeEach
    public void setUp() {
        // Очистка таблицы дружбы перед каждым тестом
        friendRepository.removeFriend(1L, 2L);
        friendRepository.removeFriend(2L, 1L);
        friendRepository.removeFriend(1L, 3L);
        friendRepository.removeFriend(3L, 1L);
    }

    @Test
    public void testAddFriend() {
        // Добавление друга
        friendRepository.addFriend(1L, 2L);

        // Проверка, что пользователь 1 добавил пользователя 2 в друзья
        List<Long> friendsOfUser1 = friendRepository.getFriendsIds(1L);
        List<Long> friendsOfUser2 = friendRepository.getFriendsIds(2L);

        assertThat(friendsOfUser1).contains(2L);
        assertThat(friendsOfUser2).isEmpty(); // Ожидается, что пользователь 2 пока не добавил пользователя 1 в друзья
    }

    @Test
    public void testRemoveFriend() {
        // Добавление и удаление друга
        friendRepository.addFriend(1L, 2L);
        friendRepository.removeFriend(1L, 2L);

        // Проверка, что пользователь 1 удалил пользователя 2 из друзей
        List<Long> friendsOfUser1 = friendRepository.getFriendsIds(1L);
        List<Long> friendsOfUser2 = friendRepository.getFriendsIds(2L);

        assertThat(friendsOfUser1).doesNotContain(2L);
        assertThat(friendsOfUser2).doesNotContain(1L);
    }

    @Test
    public void testGetFriendsIds() {
        // Добавление нескольких друзей
        friendRepository.addFriend(1L, 2L);
        friendRepository.addFriend(1L, 3L);

        // Проверка списка друзей пользователя 1
        List<Long> friendsOfUser1 = friendRepository.getFriendsIds(1L);

        assertThat(friendsOfUser1).containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    public void testGetCommonFriendsIds() {
        // Добавление друзей
        friendRepository.addFriend(1L, 2L);
        friendRepository.addFriend(2L, 3L);
        friendRepository.addFriend(1L, 3L);

        // Проверка общих друзей между пользователями 1 и 2
        List<Long> commonFriends = friendRepository.getCommonFriendsIds(1L, 2L);

        assertThat(commonFriends).containsExactlyInAnyOrder(3L);
    }

    @Test
    public void testNoCommonFriends() {
        // Добавление друзей для проверки случая без общих друзей
        friendRepository.addFriend(1L, 2L);
        friendRepository.addFriend(4L, 5L); // Предполагается, что пользователи 4 и 5 не существуют в таблице users

        // Проверка общих друзей между пользователями 1 и 4 (нет общих друзей)
        List<Long> commonFriends = friendRepository.getCommonFriendsIds(1L, 4L);

        assertThat(commonFriends).isEmpty();
    }

    @Test
    public void testEmptyFriendshipTable() {
        // Проверка работы методов с пустой таблицей дружбы

        // Проверка списка друзей для пользователя 1
        List<Long> friendsOfUser1 = friendRepository.getFriendsIds(1L);
        assertThat(friendsOfUser1).isEmpty();

        // Проверка общих друзей между пользователями 1 и 2
        List<Long> commonFriends = friendRepository.getCommonFriendsIds(1L, 2L);
        assertThat(commonFriends).isEmpty();
    }
}