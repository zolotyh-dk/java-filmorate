package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    @PostMapping
    public Film create(@RequestBody Film film) {
        return null;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        return null;
    }

    @GetMapping
    public Collection<Film> getAll() {
        return new ArrayList<>();
    }
}
