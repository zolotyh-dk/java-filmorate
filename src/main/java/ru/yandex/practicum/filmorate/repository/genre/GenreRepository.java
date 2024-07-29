package ru.yandex.practicum.filmorate.repository.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface GenreRepository {
    List<Genre> getGenresByIds(List<Integer> ids);

    Collection<Genre> getAllGenres();

    Genre getGenreById(int id);
}
