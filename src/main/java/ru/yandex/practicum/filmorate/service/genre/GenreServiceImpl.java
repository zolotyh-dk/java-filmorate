package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public Collection<Genre> getAllGenres() {
        return genreRepository.getAllGenres();
    }

    @Override
    public Genre getGenreById(int id) {
        return genreRepository.getGenreById(id);
    }
}
