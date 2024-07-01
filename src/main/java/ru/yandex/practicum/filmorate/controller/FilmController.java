package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Film saveFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на сохранение фильма. POST /films c телом: {}", film);
        final Film savedFilm = filmService.saveFilm(film);
        log.info("Возвращаем сохраненный фильм. POST /films c телом: {}", savedFilm);
        return savedFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма. PUT /films c телом: {}", film);
        final Film updatedFilm = filmService.updateFilm(film);
        log.info("Возвращаем обновленный фильм. PUT /films c телом: {}", updatedFilm);
        return updatedFilm;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен запрос на получение всех фильмов. GET /films");
        final Collection<Film> allFilms = filmService.getAllFilms();
        log.info("Возвращаем все фильмы. GET /films c телом {}", allFilms);
        return allFilms;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        log.info("Получен запрос на получение фильма по ID. GET /films/{}", id);
        final Film film = filmService.getFilmById(id);
        log.info("Возвращаем фильм. GET /films c телом: {}", film);
        return film;
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        log.info("Получен запрос на добавление лайка. PUT /films/{}/like/{}", filmId, userId);
        filmService.addLike(filmId, userId);
        log.info("Возвращаем ответ OK. PUT /films/{}/like/{}", filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        log.info("Получен запрос на удаление лайка. DELETE /films/{}/like/{}", filmId, userId);
        filmService.removeLike(filmId, userId);
        log.info("Возвращаем ответ OK. DELETE /films/{}/like/{}", filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на получение популярных фильмов. GET /films/popular?count={}", count);
        final List<Film> popularFilms = filmService.getPopularFilms(count);
        log.info("Возвращаем популярные фильмы. GET /films/popular?count={} с телом {}", count, popularFilms);
        return popularFilms;
    }
}
