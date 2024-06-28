package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film saveFilm(Film film) {
        return filmStorage.saveFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(long filmId, long userId) {
        filmStorage.getFilmById(filmId).getUsersLikeIds().add(userId);
    }

    public void removeLike(long filmId, long userId) {
        filmStorage.getFilmById(filmId).getUsersLikeIds().remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(film -> film.getUsersLikeIds().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
