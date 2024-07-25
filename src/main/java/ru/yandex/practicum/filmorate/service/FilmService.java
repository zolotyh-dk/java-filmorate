package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.RatingRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final RatingRepository ratingRepository;

    @Autowired
    public FilmService(@Qualifier("jdbcFilmRepository") FilmRepository filmRepository,
                       UserRepository userRepository,
                       GenreRepository genreRepository,
                       RatingRepository ratingRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
        this.ratingRepository = ratingRepository;
    }

    public Film saveFilm(Film film) {
        // Проверяем, что указанный в фильме рейтинг есть в БД
        final Rating rating = ratingRepository.getById(film.getRating().getId());
        if (rating == null) {
            throw new NotFoundException("Рейтинг не найдин в базе данных");
        }
        // Проверяем, что указанные в фильме жанры есть в БД
        if (film.getGenres() != null) {
            final List<Integer> genreIds = film.getGenres().stream().map(Genre::getId).toList();
            final List<Genre> genres = genreRepository.getByIds(genreIds);
            if (genreIds.size() != genres.size()) {
                throw new NotFoundException("Жанры не найдены в базе данных");
            }
        }
        return filmRepository.saveFilm(film);
    }


    public Film updateFilm(Film film) {
        final Film savedFilm = filmRepository.getFilmById(film.getId());
        // Проверяем, что указанные в фильме жанры есть в БД
        if (film.getGenres() != null) {
            final List<Integer> genreIds = film.getGenres().stream().map(Genre::getId).toList();
            final List<Genre> genres = genreRepository.getByIds(genreIds);
            if (genreIds.size() != genres.size()) {
                throw new NotFoundException("Жанры не найдены в базе данных");
            }
        }
        // Проверяем, что указанный в фильме рейтинг есть в БД
        final Rating rating = ratingRepository.getById(film.getRating().getId());
        if (rating == null) {
            throw new NotFoundException("Рейтинг не найдин в базе данных");
        }

        film.setUsersLikeIds(savedFilm.getUsersLikeIds());
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
        filmRepository.addLike(filmId, userId);
        log.info("Пользователь с ID: {} поставил лайк фильму c ID: {}", userId, filmId);
    }

    public void removeLike(long filmId, long userId) {
        userRepository.getUserById(userId); //проверка существования пользователя в хранилище
        filmRepository.removeLike(filmId, userId);
        log.info("Пользователь с ID: {} удалил лайк у фильма c ID: {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmRepository.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getUsersLikeIds().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
