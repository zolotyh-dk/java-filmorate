package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {
//    private static final NamedParameterJdbcOperations jdbc;

    @Override
    public Film saveFilm(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", film.getName());
        //остальные параметры

//        jdbc.update("INSERT INTO films (name) VALUES (:name)", params, keyHolder, new String[]{"id"});
        //создать связи фильм - жанры
        //batch
        film.setId(keyHolder.getKeyAs(Long.class));
        return film;
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
