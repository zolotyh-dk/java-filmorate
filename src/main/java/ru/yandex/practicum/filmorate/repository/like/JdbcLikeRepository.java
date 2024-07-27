package ru.yandex.practicum.filmorate.repository.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class JdbcLikeRepository implements LikeRepository {
    private final NamedParameterJdbcOperations jdbc;

    public NamedParameterJdbcOperations getJdbc() {
        return jdbc;
    }

    @Override
    public void addLike(long filmId, long userId) {
        //Проверяем нет ли уже лайка от этого пользователя этому фильму
        final String checkSql = """
                SELECT COUNT(*)
                FROM likes
                WHERE film_id = :filmId AND user_id = :userId
                """;
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("filmId", filmId);
        params.addValue("userId", userId);
        int count = jdbc.queryForObject(checkSql, params, Integer.class);

        //Если строки нет - делаем INSERT
        if (count == 0) {
            String insertSql = """
                    INSERT INTO likes (film_id, user_id)
                    VALUES (:filmId, :userId)
                    """;
            jdbc.update(insertSql, params);
        }else {
            //Если такая строка уже есть - не делаем ничего
            log.debug("В БД уже содержится лайк от пользователя ID:{} фильму ID:{}", userId, filmId);
        }
    }

    @Override
    public void removeLike(long filmId, long userId) {
        final String sql = """
                DELETE FROM likes
                WHERE film_id = :filmId AND user_id = :userId
                """;
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("filmId", filmId);
        params.addValue("userId", userId);
        jdbc.update(sql, params);
    }
}
