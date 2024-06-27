package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

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
//        return filmStorage.getAllFilms().stream()
//                .sorted((film, other) -> film.getUsersLikeIds() - film.getUsersLikeIds())
        return null;
    }
}
