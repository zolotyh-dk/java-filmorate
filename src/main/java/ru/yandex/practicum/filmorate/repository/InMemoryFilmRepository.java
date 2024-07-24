package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
            throw new FilmNotFoundException("Фильм с ID: " + id + " не найден.");
        }
        return film;
    }
}
