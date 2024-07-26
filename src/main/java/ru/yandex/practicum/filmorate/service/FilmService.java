package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;

    @Autowired
    public FilmService(@Qualifier("jdbcFilmRepository") FilmRepository filmRepository,
                       UserRepository userRepository,
                       GenreRepository genreRepository,
                       MpaRepository mpaRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
    }

    public Film saveFilm(Film film) {
        // Проверяем, что указанный в фильме рейтинг есть в БД
        if (film.getMpa() != null) {
            int ratingId = film.getMpa().getId();
            final Mpa savedMpa = mpaRepository.getMpaById(ratingId).orElseThrow(() ->
                    new NotFoundException("Рейтинг MPA с ID: " + ratingId + " не найден в базе данных"));
            log.debug("Запросили рейтинг фильма из БД. Рейтинг с ID: {} найден: {}", ratingId, savedMpa);
        }
        // Проверяем, что указанные в фильме жанры есть в БД
        if (film.getGenres() != null) {
            final List<Integer> genreIds = film.getGenres().stream().map(Genre::getId).toList();
            final List<Genre> savedGenres = genreRepository.getGenresByIds(genreIds);
            log.debug("Запросили жанры фильма из БД для проверки их существования с ID:{} жанры = {}", genreIds, savedGenres);;
            if (genreIds.size() != savedGenres.size()) {
                throw new NotFoundException("Жанры c ID: " + genreIds + " не найдены в базе данных");
            }
        }
        return filmRepository.saveFilm(film);
    }


    public Film updateFilm(Film film) {
        final Film savedFilm = filmRepository.getFilmById(film.getId());

        // Проверяем, что указанный в фильме рейтинг есть в БД
        if (film.getMpa() != null) {
            int ratingId = film.getMpa().getId();
            final Mpa savedMpa = mpaRepository.getMpaById(ratingId).orElseThrow(() ->
                    new NotFoundException("Рейтинг MPA с ID: " + ratingId + " не найден в базе данных"));
            log.debug("Запросили рейтинг фильма из БД. Рейтинг с ID:{} найден: {}", ratingId, savedMpa);
        }

        // Проверяем, что указанные в фильме жанры есть в БД
        if (film.getGenres() != null) {
            final List<Integer> genreIds = film.getGenres().stream().map(Genre::getId).toList();
            final List<Genre> genres = genreRepository.getGenresByIds(genreIds);
            if (genreIds.size() != genres.size()) {
                throw new NotFoundException("Жанры c ID: " + genreIds + " не найдены в базе данных");
            }
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
        return filmRepository.getTopPopular(count);
    }
}
