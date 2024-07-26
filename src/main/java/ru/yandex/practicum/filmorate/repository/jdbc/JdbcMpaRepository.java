package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcMpaRepository implements MpaRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Mpa> getMpaById(int id) {
        final String sql = """
                SELECT id, name
                FROM mpa
                WHERE id = :id
                """;
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        return jdbc.query(sql, params, new MpaRowMapper()).stream().findFirst();
    }
}
