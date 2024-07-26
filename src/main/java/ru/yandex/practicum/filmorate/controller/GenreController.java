package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> getAllGenres() {
        log.info("Получен запрос на получение всех жанров. GET /genres");
        final Collection<Genre> allGenres = genreService.getAllGenres();
        log.info("Возвращаем все жанры. GET /genres c телом {}", allGenres);
        return allGenres;
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        log.info("Получен запрос на получение жанра по ID. GET /genres/{}", id);
        final Genre genre = genreService.getGenreById(id);
        log.info("Возвращаем жанр. GET /films/{} c телом: {}", id, genre);
        return genre;
    }
}
