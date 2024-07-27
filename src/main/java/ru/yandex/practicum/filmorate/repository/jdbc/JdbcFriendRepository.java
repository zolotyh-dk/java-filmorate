package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.repository.FriendRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcFriendRepository implements FriendRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public void addFriend(long userId, long friendId) {
        //Проверяем нет ли уже записи о дружбе этих пользователей
        final String checkSql = """
                SELECT COUNT(*)
                FROM friendship
                WHERE user_id = :userId AND friend_id = :friendId
                """;
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("friendId", friendId);
        int count = jdbc.queryForObject(checkSql, params, Integer.class);

        //Если строки нет - делаем INSERT
        if (count == 0) {
            String insertSql = """
                    INSERT INTO friendship (user_id, friend_id)
                    VALUES (:userId, :friendId)
                    """;
            jdbc.update(insertSql, params);
        } else {
            //Если такая строка уже есть - не делаем ничего
            log.debug("В БД уже содержится дружба пользователя ID:{} с другом ID:{}", userId, friendId);
        }

    }

    @Override
    public void removeFriend(long userId, long friendId) {
        final String sql = """
                DELETE FROM friendship
                WHERE user_id = :userId AND friend_id = :friendId
                """;
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("friendId", friendId);
        jdbc.update(sql, params);
    }

    @Override
    public List<Integer> getFriendsIds(long id) {
        final String sql = """
                SELECT friend_id
                FROM friendship
                WHERE user_id = :id
                """;
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        return jdbc.queryForList(sql, params, Integer.class);
    }

    @Override
    public List<Integer> getCommonFriendsIds(long userId, long otherId) {
        final String sql = """
            SELECT f1.friend_id
            FROM friendship AS f1
            JOIN friendship AS f2 ON f1.friend_id = f2.friend_id
            WHERE f1.user_id = :userId AND f2.user_id = :otherId
            """;

        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("otherId", otherId);
        return jdbc.queryForList(sql, params, Integer.class);
    }
}
