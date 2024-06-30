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
        log.debug("Метод FilmService.saveFilm. Сохраняем фильм={}", film);
        return filmStorage.saveFilm(film);
    }

    public Film updateFilm(Film film) {
        log.debug("Метод FilmService.updateFilm. Обновляем фильм={}", film);
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> getAllFilms() {
        log.debug("Метод FilmService.getAllFilms. Получаем все фильмы.");
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        log.debug("Метод FilmService.getFilmById. Получаем фильм по id={}", id);
        return filmStorage.getFilmById(id);
    }

    public void addLike(long filmId, long userId) {
        log.debug("Метод FilmService.addLike. Добавляем лайк от пользователя с id={} фильму с id={}", userId, filmId);
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId).getUsersLikeIds().add(userId);
    }

    public void removeLike(long filmId, long userId) {
        log.debug("Метод FilmService.removeLike. Убираем лайк от пользователя с id={} у фильма с id={}", userId, filmId);
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId).getUsersLikeIds().remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        log.debug("Метод FilmService.getPopularFilms. Получаем {} популярных фильмов.", count);
        final List<Film> popularFilms = filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getUsersLikeIds().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
        log.debug("Метод FilmService.getPopularFilms. Возвращаем список популярных фильмов={}", popularFilms);
        return popularFilms;
    }
}
