package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return null;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return null;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return new ArrayList<>();
    }
}
