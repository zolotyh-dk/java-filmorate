package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
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

    public Film getFilmById(long id) {
        log.debug("Метод FilmService.getFilmById. В параметрах метода id={}", id);
        return filmStorage.getFilmById(id);
    }

    public void addLike(long filmId, long userId) {
        filmStorage.getFilmById(filmId).getUsersLikeIds().add(userId);
    }

    public void removeLike(long filmId, long userId) {
        filmStorage.getFilmById(filmId).getUsersLikeIds().remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        final List<Film> popularFilms = filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getUsersLikeIds().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
        log.debug("Метод FilmService.getPopularFilms. Возвращаем список популярных фильмов={}", popularFilms);
        return popularFilms;
    }
}
