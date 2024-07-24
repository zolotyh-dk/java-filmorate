package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    @Autowired
    public FilmService(FilmRepository filmRepository, UserRepository userRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
    }

    public Film saveFilm(Film film) {
        return filmRepository.saveFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmRepository.updateFilm(film);
    }

    public Collection<Film> getAllFilms() {
        return filmRepository.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmRepository.getFilmById(id);
    }

    public void addLike(long filmId, long userId) {
        userRepository.getUserById(userId); //проверка существования пользователя в хранилище
        final Film film = filmRepository.getFilmById(filmId);
        film.getUsersLikeIds().add(userId);
        log.info("Пользователь с ID: {} поставил лайк фильму: {}", userId, film);
    }

    public void removeLike(long filmId, long userId) {
        userRepository.getUserById(userId); //проверка существования пользователя в хранилище
        final Film film = filmRepository.getFilmById(filmId);
        film.getUsersLikeIds().remove(userId);
        log.info("Пользователь с ID: {} удалил лайк у фильма: {}", userId, film);
    }

    public List<Film> getPopularFilms(int count) {
        return filmRepository.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getUsersLikeIds().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
