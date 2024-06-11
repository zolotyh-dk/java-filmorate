package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
        log.debug("Сохранен фильм: {}", film);
        log.debug("Хранилище фильмов теперь в состоянии: {}", films);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Метод updateFilm. В теле запроса фильм: {}", film);
        final int id = film.getId();
        final Film savedFilm = films.get(id);
        if (savedFilm == null) {
            log.debug("Фильм с id={} не найден", id);
            return null;
        }
        films.put(id, film);
        log.debug("Обновлен фильм: {}", film);
        log.debug("Хранилище фильмов теперь в состоянии: {}", films);
        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.debug("Метод getAllUsers");
        return films.values();
    }
}
