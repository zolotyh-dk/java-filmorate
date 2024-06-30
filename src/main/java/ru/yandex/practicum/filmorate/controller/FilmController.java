package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film saveFilm(@Valid @RequestBody Film film) {
        log.debug("Метод FilmController.saveFilm. В теле запроса film={}", film);
        return filmService.saveFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Метод FilmController.updateFilm. В теле запроса film={}", film);
        return filmService.updateFilm(film);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.debug("Метод FilmController.getAllFilms");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        log.debug("Метод FilmController.getFilmById. В пути запроса id={}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        log.debug("Метод FilmController.addLike. В пути запроса filmId={}, userId={}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        log.debug("Метод FilmController.removeLike. В пути запроса filmId={}, userId={}", filmId, userId);
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.debug("Метод FilmController.getPopularFilms. В параметрах запроса count={}", count);
        return filmService.getPopularFilms(count);
    }
}
