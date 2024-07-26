package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Film saveFilm(Film film) {
        final String sql = """
                INSERT INTO films (name, description, release_date, duration, mpa_id)
                VALUES (:name, :description, :releaseDate, :duration, :mpaId)
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("releaseDate", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        Mpa mpa = film.getMpa();
        if (mpa != null) {
            params.addValue("mpaId", film.getMpa().getId());
        } else {
            params.addValue("mpaId", null);
        }
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        long filmId = keyHolder.getKey().longValue();
        film.setId(filmId);
        log.info("Фильму присвоен в БД ID: {}", filmId);
        final List<Integer> genreIds = film.getGenres().stream().map(Genre::getId).toList();
        createFilmGenreRelationships(filmId, genreIds);
        return film;
    }

    private void createFilmGenreRelationships(long filmId, List<Integer> genreIds) {
        final String sql = """
                INSERT INTO films_genres (film_id, genre_id)
                VALUES (:filmId, :genreId)
                """;
        final List<MapSqlParameterSource> batchParams = new ArrayList<>();
        for (int genreId : genreIds) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("filmId", filmId);
            params.addValue("genreId", genreId);
            batchParams.add(params);
        }
        log.info("Добавляем связи фильм - жанры в БД: {}", batchParams);
        jdbc.batchUpdate(sql, batchParams.toArray(new MapSqlParameterSource[0]));
    }

    @Override
    public Film updateFilm(Film film) {
        final String updateFilmSql = """
                UPDATE films
                SET name = :name, description = :description, release_date = :releaseDate, duration = :duration, rating_id = :ratingId
                WHERE id = :filmId
                """;

        final String deleteGenresSql = """
                DELETE FROM films_genres
                WHERE film_id = :filmId
                """;

        final String insertNewGenresSql = """
                INSERT INTO films_genres (film_id, genre_id)
                VALUES (:filmId, :genreId)""";

        // Обновляем информацию о фильме
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("filmId", film.getId());
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("releaseDate", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("rating_id", film.getMpa().getId());
        jdbc.update(updateFilmSql, params);

        // Удаляем существующие связи фильм - жанры
        jdbc.update(deleteGenresSql, params);

        // Выполняем batch-вставку новых связей фильм - жанры
        List<SqlParameterSource> batchParams = new ArrayList<>();
        for (Genre genre : film.getGenres()) {
            MapSqlParameterSource batchParam = new MapSqlParameterSource();
            batchParam.addValue("filmId", film.getId());
            batchParam.addValue("genreId", genre.getId());
            batchParams.add(batchParam);
        }
        jdbc.batchUpdate(insertNewGenresSql, batchParams.toArray(new SqlParameterSource[0]));

        return film;
    }

    /*1. получить все жанры (их мало)
      2. получить все фильмы с рейтингом, но без жанра
      3. получить связи жанры - фильмы static record GenreRelation(film_id, genre_id)
      4. объединить */
    @Override
    public Collection<Film> getAllFilms() {
        // Получаем все жанры всех жанров
        final String getGenresSql = """
                                    SELECT id, name
                                    FROM genres
                """;
        final List<Genre> genres = jdbc.query(getGenresSql, new GenreRowMapper());

        // Получение всех фильмов с рейтингом
        final String getFilmsSql = """
                                    SELECT f.id,
                                           f.name,
                                           f.description,
                                           f.release_date,
                                           f.duration,
                                           r.id as rating_id,
                                           r.name as rating_name
                                    FROM films f
                                    JOIN ratings r ON f.rating_id = r.id
                """;
        final List<Film> films = jdbc.query(getFilmsSql, new FilmRowMapper());

        // Получение всех связей между фильмами и жанрами
        final String getFilmGenreRelationsSql = """
                SELECT film_id, genre_id
                FROM films_genres
                """;
        List<FilmGenreRelation> filmGenreRelations = jdbc.query(getFilmGenreRelationsSql, (resultSet, rowNum) ->
                new FilmGenreRelation(resultSet.getLong("film_id"), resultSet.getInt("genre_id")));

        // Создаем мапы для удобного доступа к фильмам и жанрам по их id
        final Map<Long, Film> filmMap = films.stream().collect(Collectors.toMap(Film::getId, film -> film));
        final Map<Integer, Genre> genreMap = genres.stream().collect(Collectors.toMap(Genre::getId, genre -> genre));

        // Добавление жанров к соответствующим фильмам
        for (FilmGenreRelation relation : filmGenreRelations) {
            Film film = filmMap.get(relation.filmId());
            if (film != null) {
                Genre genre = genreMap.get(relation.genreId());
                if (genre != null) {
                    film.addGenre(genre);
                }
            }
        }

        return films;
    }

    // Класс для представления связи между фильмом и жанром
    private record FilmGenreRelation(long filmId, int genreId) {
    }

    @Override
    public Film getFilmById(long id) {
        String sql = """
                   SELECT f.id as film_id,
                          f.name as film_name,
                          f.description,
                          f.release_date,
                          f.duration,
                          m.id as mpa_id,
                          m.name as mpa_name,
                          g.id as genre_id,
                          g.name as genre_name
                   FROM films f
                   JOIN mpa AS m ON f.mpa_id = m.id
                   LEFT JOIN films_genres fg ON f.id = fg.film_id
                   LEFT JOIN genres g ON fg.genre_id = g.id
                   WHERE f.id = :id
                """;
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        return jdbc.query(sql, params, new FilmResultSetExtractor());
    }

    private static class FilmResultSetExtractor implements ResultSetExtractor<Film> {
        @Override
        public Film extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            Film.FilmBuilder filmBuilder = null;
            Set<Genre> genres = new HashSet<>();

            while (resultSet.next()) {
                if (filmBuilder == null) {
                    Mpa mpa = Mpa.builder()
                            .id(resultSet.getInt("mpa_id"))
                            .name(resultSet.getString("mpa_name"))
                            .build();

                    log.debug("Извлекли рейтинг фильма из БД: {}", mpa);

                    filmBuilder = Film.builder()
                            .id(resultSet.getLong("film_id"))
                            .name(resultSet.getString("film_name"))
                            .description(resultSet.getString("description"))
                            .releaseDate(resultSet.getDate("release_date").toLocalDate())
                            .duration(resultSet.getInt("duration"))
                            .mpa(mpa);
                }

                int genreId = resultSet.getInt("genre_id");
                if (!resultSet.wasNull()) {
                    Genre genre = Genre.builder()
                            .id(genreId)
                            .name(resultSet.getString("genre_name"))
                            .build();
                    log.debug("Извлекли жанр фильма из БД: {}", genre);
                    genres.add(genre);
                }
            }

            if (filmBuilder != null) {
                throw new NotFoundException("Фильм не найден");
            }
            Film film = filmBuilder.build();
            film.setGenres(genres);
            return film;
        }
    }

    @Override
    public void addLike(long filmId, long userId) {
        //Проверяем нет ли уже лайка от этого пользователя этому фильму
        String checkSql = """
                SELECT COUNT(*)
                FROM likes
                WHERE film_id = :filmId AND user_id = :userId
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
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
        }
        //Если такая строка уже есть - не делаем ничего
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sql = """
                DELETE FROM likes
                WHERE film_id = :filmId AND user_id = :userId
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("filmId", filmId);
        params.addValue("userId", userId);
        jdbc.update(sql, params);
    }

    @Override
    public List<Film> getTopPopular(int count) {
        // SQL-запрос для получения самых популярных фильмов с ограничением по количеству
        String sql = """
                SELECT f.id,
                       f.name,
                       f.description,
                       f.release_date,
                       f.duration,
                       r.id AS rating_id,
                       r.name AS rating_name,
                       l.like_count
                FROM films f
                JOIN ratings r ON f.rating_id = r.id
                LEFT JOIN (
                    SELECT film_id, COUNT(*) AS like_count
                    FROM likes
                    GROUP BY film_id
                ) AS l ON f.id = l.film_id
                ORDER BY l.like_count DESC
                LIMIT :count
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("count", count);

        return jdbc.query(sql, params, new FilmRowMapper());
    }
}
