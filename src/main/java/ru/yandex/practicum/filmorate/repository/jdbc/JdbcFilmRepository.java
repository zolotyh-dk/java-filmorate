package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
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
        final Mpa mpa = film.getMpa();
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
        if (film.getGenres() != null) {
            final List<Integer> genreIds = film.getGenres().stream().map(Genre::getId).toList();
            createFilmGenreRelationships(filmId, genreIds);
        }
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
                SET name = :name, description = :description, release_date = :releaseDate, duration = :duration, mpa_id = :mpaId
                WHERE id = :filmId
                """;

        final String deleteGenresSql = """
                DELETE FROM films_genres
                WHERE film_id = :filmId
                """;

        final String insertNewGenresSql = """
                INSERT INTO films_genres (film_id, genre_id)
                VALUES (:filmId, :genreId)""";

        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("filmId", film.getId());
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("releaseDate", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpaId", film.getMpa().getId());
        log.debug("Обновляем информацию о фильме {}", params);
        jdbc.update(updateFilmSql, params);

        log.debug("Удаляем существующие связи фильм-жанры");
        jdbc.update(deleteGenresSql, params);

        if (film.getGenres() != null) {
            List<SqlParameterSource> batchParams = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                MapSqlParameterSource batchParam = new MapSqlParameterSource();
                batchParam.addValue("filmId", film.getId());
                batchParam.addValue("genreId", genre.getId());
                batchParams.add(batchParam);
            }
            log.debug("Выполняем batch-вставку новых связей фильм - жанры: {}", batchParams);
            jdbc.batchUpdate(insertNewGenresSql, batchParams.toArray(new SqlParameterSource[0]));
        }
        log.debug("Возвращаем обновленный фильм: {}", film);
        return film;
    }

    /*1. получить все жанры (их мало)
      2. получить все фильмы с рейтингом, но без жанра
      3. получить связи жанры - фильмы static record GenreRelation(film_id, genre_id)
      4. объединить */
    @Override
    public Collection<Film> getAllFilms() {
        // Получаем все жанры
        final String getGenresSql = """
                                    SELECT id, name
                                    FROM genres
                """;
        final List<Genre> genres = jdbc.query(getGenresSql, new GenreRowMapper());
        log.debug("Получили все жанры из БД, размер списка: {}", genres.size());

        // Получаем все фильмы с рейтингом, но без жанров
        final String getFilmsSql = """
                                    SELECT f.id,
                                           f.name,
                                           f.description,
                                           f.release_date,
                                           f.duration,
                                           m.id as mpa_id,
                                           m.name as mpa_name
                                    FROM films f
                                    LEFT JOIN mpa AS m ON f.mpa_id = m.id
                """;
        final List<Film> films = jdbc.query(getFilmsSql, new FilmRowMapper());
        log.debug("Получили все фильмы из БД, размер списка: {}", films.size());

        // Получение всех связей между фильмами и жанрами
        final String getFilmGenreRelationsSql = """
                SELECT film_id, genre_id
                FROM films_genres
                """;
        final List<FilmGenreRelation> filmGenreRelations = jdbc.query(getFilmGenreRelationsSql, (resultSet, rowNum) ->
                new FilmGenreRelation(resultSet.getLong("film_id"), resultSet.getInt("genre_id")));
        log.debug("Получили все связи фильм-жанр из БД, размер списка: {}", filmGenreRelations.size());

        // Создаем мапы для удобного доступа к фильмам и жанрам по их id (позволяет избежать вложенных циклов ниже)
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
        log.debug("Возвращаем все фильмы из БД, размер списка: {}", films.size());
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
        log.debug("Ищем в базе данных фильм с ID: {}", id);
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

            if (filmBuilder == null) {
                throw new FilmNotFoundException("Фильм не найден в базе данных");
            }

            Film film = filmBuilder.build();
            film.setGenres(genres);
            log.debug("Получили из базы данных фильм: {}", film);
            return film;
        }
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

    @Override
    public List<Film> getTopPopular(int count) {
        // SQL-запрос для получения самых популярных фильмов с ограничением по количеству
        final String sql = """
                SELECT f.id,
                       f.name,
                       f.description,
                       f.release_date,
                       f.duration,
                       m.id AS mpa_id,
                       m.name AS mpa_name,
                       l.like_count
                FROM films f
                JOIN mpa AS m ON f.mpa_id = m.id
                LEFT JOIN (
                    SELECT film_id, COUNT(*) AS like_count
                    FROM likes
                    GROUP BY film_id
                ) AS l ON f.id = l.film_id
                ORDER BY l.like_count DESC
                LIMIT :count
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("count", count);

        return jdbc.query(sql, params, new FilmRowMapper());
    }
}
