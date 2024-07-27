package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
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
        log.debug("Ищем в БД жанры по списку ID, размер списка ID: {}", genreIds.size());
        final List<Genre> genres = jdbc.query(sql, params, new GenreRowMapper());
        log.debug("Получили из БД жанры по списку ID, получили список размером: {}", genres.size());
        return genres;
    }


    @Override
    public Collection<Genre> getAllGenres() {
        final String sql = """
                SELECT *
                FROM genres
                """;
        final List<Genre> genres = jdbc.query(sql, new GenreRowMapper());
        log.debug("Получили из БД все жанры, размер списка: {}", genres.size());
        return genres;
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
        log.debug("Ищем в базе данных жанр с ID: {}", id);
        return jdbc.query(sql, params, new GenreRowMapper()).stream().findFirst().orElseThrow(() ->
                new GenreNotFoundException("Жанр с ID " + id + " не найден в базе данных"));
    }
}
