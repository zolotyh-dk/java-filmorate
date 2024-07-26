package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmRepository {
    Film saveFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> getAllFilms();

    Film getFilmById(long id);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getTopPopular(int count);
}
