package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private int generatorId = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film saveFilm(@Valid @RequestBody Film film) {
        log.debug("Метод saveFilm. В теле запроса фильм: {}", film);
        final int id = ++generatorId;
        film.setId(id);
        films.put(id, film);
        log.debug("Сохранен фильм: {}\nХранилище фильмов теперь в состоянии: {}", film, films);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Метод updateFilm. В теле запроса фильм: {}", film);
        final int id = film.getId();
        final Film savedFilm = films.get(id);
        if (savedFilm == null) {
            throw new FilmNotFoundException("Фильм с id=" + id + " не найден.");
        }
        films.put(id, film);
        log.debug("Обновлен фильм: {}\nХранилище фильмов теперь в состоянии: {}", film, films);
        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.debug("Метод getAllFilms");
        Collection<Film> allFilms = films.values();
        log.debug("Возвращаем коллекцию фильмов: {}", allFilms);
        return allFilms;
    }
}
