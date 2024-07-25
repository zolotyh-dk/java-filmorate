package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations jdbc;

    public List<Genre> getByIds(List<Integer> genreIds) {
        String sql = "SELECT * FROM genres " +
                     "WHERE id IN (:genreIds)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("genreIds", genreIds);

        RowMapper<Genre> rowMapper = new GenreRowMapper();
        return jdbc.query(sql, params, rowMapper);
    }


}
