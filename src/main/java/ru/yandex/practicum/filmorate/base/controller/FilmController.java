package ru.yandex.practicum.filmorate.base.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.base.exception.ValidationException;
import ru.yandex.practicum.filmorate.base.model.Film;

import java.time.LocalDate;
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
    public Film saveFilm(@RequestBody Film film) throws ValidationException {
        log.debug("Метод saveFilm. В теле запроса фильм: {}", film);

        try {
            validateFilm(film);
        } catch (ValidationException e) {
            log.error("Ошибка валидации фильма: {}", e.getMessage());
            throw e;
        }

        final int id = ++generatorId;
        film.setId(id);
        films.put(id, film);
        log.debug("Сохранен фильм: {}", film);
        log.debug("Хранилище фильмов теперь в состоянии: {}", films);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        log.debug("Метод updateFilm. В теле запроса фильм: {}", film);

        try {
            validateFilm(film);
        } catch (ValidationException e) {
            log.error("Ошибка валидации фильма: {}", e.getMessage());
            throw e;
        }

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
        log.debug("Метод getAllFilms");
        return films.values();
    }

    private void validateFilm(Film film) {
        final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

        if (film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
