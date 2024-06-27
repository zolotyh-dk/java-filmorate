package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public void addLike(long filmId, long userId) {
        filmStorage.getFilmById(filmId).getUsersLikeIds().add(userId);
    }

    public void removeLike(long filmId, long userId) {
        filmStorage.getFilmById(filmId).getUsersLikeIds().remove(userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(film -> film.getUsersLikeIds().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
