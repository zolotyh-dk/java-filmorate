package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film saveFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> getAllFilms();
}
