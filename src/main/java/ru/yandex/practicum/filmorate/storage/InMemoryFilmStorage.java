package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private int generatorId = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film saveFilm(Film film) {
        final int id = ++generatorId;
        film.setId(id);
        films.put(id, film);
        log.debug("Сохранен фильм: {}\nХранилище фильмов теперь в состоянии: {}", film, films);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        final int id = film.getId();
        final Film savedFilm = films.get(id);
        if (savedFilm == null) {
            throw new FilmNotFoundException("Фильм с id=" + id + " не найден.");
        }
        films.put(id, film);
        log.debug("Обновлен фильм: {}\nХранилище фильмов теперь в состоянии: {}", film, films);
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        Collection<Film> allFilms = films.values();
        log.debug("Возвращаем коллекцию фильмов: {}", allFilms);
        return allFilms;
    }
}
