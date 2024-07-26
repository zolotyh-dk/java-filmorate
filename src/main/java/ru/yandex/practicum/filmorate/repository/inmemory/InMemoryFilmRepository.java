package ru.yandex.practicum.filmorate.repository.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmRepository implements FilmRepository {
    private long generatorId = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film saveFilm(Film film) {
        final long id = ++generatorId;
        film.setId(id);
        log.info("Фильму присвоен ID: {}", id);
        films.put(id, film);
        log.info("Фильм сохранен в хранилище: {}", films);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        final long id = film.getId();
        final Film savedFilm = getFilmById(id);
        film.setUsersLikeIds(savedFilm.getUsersLikeIds());
        films.put(id, film);
        log.info("Фильм обновлен в хранилище: {}", films);
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film getFilmById(long id) {
        final Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с ID: " + id + " не найден.");
        }
        return film;
    }

    @Override
    public void addLike(long filmId, long userId) {
        final Film film = getFilmById(filmId);
        film.getUsersLikeIds().add(userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        final Film film = getFilmById(filmId);
        film.getUsersLikeIds().remove(userId);
    }

    @Override
    public List<Film> getTopPopular(int count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getUsersLikeIds().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }


}
