package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    FilmStorage filmStorage = new InMemoryFilmStorage();

    @PostMapping
    public Film saveFilm(@Valid @RequestBody Film film) {
        log.debug("Метод FilmController.saveFilm. В теле запроса фильм: {}", film);
        return filmStorage.saveFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Метод FilmController.updateFilm. В теле запроса фильм: {}", film);
        return filmStorage.updateFilm(film);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.debug("Метод FilmController.getAllFilms");
        return filmStorage.getAllFilms();
    }
}
