package ru.yandex.practicum.filmorate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Autowired
    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Film saveFilm(Film film) {
        final String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                            "VALUES (:name, :description, :releaseDate, :duration, :ratingId)";

        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("releaseDate", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("ratingId", film.getRating().getId());

        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        long filmId = keyHolder.getKey().longValue();
        film.setId(filmId);

        final List<Integer> genreIds = film.getGenres().stream().map(Genre::getId).toList();
        createFilmGenreRelationships(filmId, genreIds);
        return film;
    }

    private void createFilmGenreRelationships(long filmId, List<Integer> genreIds) {
        final String sql = "INSERT INTO films_genres (film_id, genre_id) " +
                           "VALUES (:filmId, :genreId)";
        final List<MapSqlParameterSource> batchParams = new ArrayList<>();
        for (int genreId : genreIds) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("filmId", filmId);
            params.addValue("genreId", genreId);
            batchParams.add(params);
        }
        jdbc.batchUpdate(sql, batchParams.toArray(new MapSqlParameterSource[0]));
    }






























    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public Collection<Film> getAllFilms() {
        //1. получить все жанры (их мало)
        //2. получить все фильмы с рейтингом, но без жанра
        //3. получить связи жанры - фильмыы static record GenreRelation(film_id, genre_id)
        //Объединить
        return null;
    }

    @Override
    public Film getFilmById(long id) {
        //select from films join MPA join GENRES where film_id = :id
        //ResultSetExtractor
        //ResultSet rs
        //Film film = null;
        //while(rs.next()) {
        //  if(film == null) {
        //      film = new Film(); //заполняем все поля, а жанры пустые
        //  }
        // добавляем жанры
        // }
        //return film;
        return null;
    }

//    @Override
    public void addLike(long filmId, long userId) {
        //merge если запись есть то она обновится (то есть ничего не произойдет)
        //а если нет то она будет создана
    }

//    @Override
    public void deleteLike(long filmId, long userId) {

    }

//    @Override
    public List<Film>getTopPopular(int count) {
        //сортировка и лимит
        return List.of();
    }
}
