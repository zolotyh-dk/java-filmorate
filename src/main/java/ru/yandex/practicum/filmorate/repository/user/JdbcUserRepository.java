package ru.yandex.practicum.filmorate.repository.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public User saveUser(User user) {
        final String sql = """
                INSERT INTO users (email, login, name, birthday)
                VALUES(:email, :login, :name, :birthday)
                """;
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("name", user.getName());
        params.addValue("birthday", user.getBirthday());
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        long userId = keyHolder.getKeyAs(Long.class);
        user.setId(userId);
        log.info("Пользователю присвоен в БД ID: {}", userId);
        return user;
    }

    @Override
    public User updateUser(User user) {
        final String sql = """
                UPDATE users
                SET email = :email, login = :login, name = :name, birthday = :birthday
                WHERE id = :userId
                """;
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", user.getId());
        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("name", user.getName());
        params.addValue("birthday", user.getBirthday());
        log.debug("Обновляем информацию о пользователе {}", params);
        jdbc.update(sql, params);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        final String sql = """
                SELECT *
                FROM users
                """;
        final List<User> users = jdbc.query(sql, new UserRowMapper());
        log.debug("Получили всех пользователей из БД, размер списка: {}", users.size());
        return users;
    }

    @Override
    public User getUserById(long id) {
        final String sql = """
                SELECT *
                FROM users
                WHERE id = :id
                """;
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        log.debug("Ищем в базе данных пользователя с ID: {}", id);
        return jdbc.query(sql, params, new UserRowMapper()).stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + id + " не найден в БД."));
    }

    @Override
    public List<User> getUsersByIds(List<Integer> userIds) {
        final String sql = """
                SELECT *
                FROM users
                WHERE id IN (:userIds)
                """;
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userIds", userIds);
        log.debug("Ищем в БД пользователей по списку ID, размер списка ID: {}", userIds.size());
        final List<User> users = jdbc.query(sql, params, new UserRowMapper());
        log.debug("Получили из БД пользователей по списку ID, получили список размером: {}", users.size());
        return users;
    }
}
