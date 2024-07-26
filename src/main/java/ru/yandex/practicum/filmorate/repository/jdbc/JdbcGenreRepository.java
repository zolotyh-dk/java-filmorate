package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<Genre> getGenresByIds(List<Integer> genreIds) {
        final String sql = """
                SELECT *
                FROM genres
                WHERE id IN (:genreIds)
                """;
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("genreIds", genreIds);
        return jdbc.query(sql, params, new GenreRowMapper());
    }


    @Override
    public Collection<Genre> getAllGenres() {
        final String sql = """
                SELECT *
                FROM genres
                """;
        return jdbc.query(sql, new GenreRowMapper());
    }

    @Override
    public Genre getGenreById(int id) {
        final String sql = """
                SELECT *
                FROM genres
                WHERE id = :id
                """;
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        return jdbc.queryForObject(sql, params, new GenreRowMapper());
    }
}
