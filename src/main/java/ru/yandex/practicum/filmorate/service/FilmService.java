package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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
        return filmStorage.getFilmById(id);
    }

    public void addLike(long filmId, long userId) {
        userStorage.getUserById(userId); //проверка существования пользователя в хранилище
        final Film film = filmStorage.getFilmById(filmId);
        film.getUsersLikeIds().add(userId);
        log.info("Пользователь с ID: {} поставил лайк фильму: {}", userId, film);
    }

    public void removeLike(long filmId, long userId) {
        userStorage.getUserById(userId); //проверка существования пользователя в хранилище
        final Film film = filmStorage.getFilmById(filmId);
        film.getUsersLikeIds().remove(userId);
        log.info("Пользователь с ID: {} удалил лайк у фильма: {}", userId, film);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getUsersLikeIds().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
