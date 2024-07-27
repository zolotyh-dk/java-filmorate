package ru.yandex.practicum.filmorate.repository.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class JdbcMpaRepository implements MpaRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Mpa getMpaById(int id) {
        final String sql = """
                SELECT id, name
                FROM mpa
                WHERE id = :id
                """;
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        return jdbc.query(sql, params, new MpaRowMapper()).stream().findFirst().orElseThrow(() ->
                new NotFoundException("Рейтинг с ID: " + id + " не найден в базе данных"));
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        final String sql = """
                SELECT *
                FROM mpa
                """;
        return jdbc.query(sql, new MpaRowMapper());
    }
}
