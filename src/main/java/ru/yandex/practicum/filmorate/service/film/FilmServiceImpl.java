package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.RequestException;
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
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;

    @Autowired
    public FilmServiceImpl(FilmRepository filmRepository,
                           GenreRepository genreRepository,
                           MpaRepository mpaRepository) {
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
    }

    @Override
    public Film saveFilm(Film film) {
        // Проверяем, что указанный в фильме рейтинг есть в БД
        if (film.getMpa() != null) {
            int mpaId = film.getMpa().getId();
            //Оборачиваем NotFoundException т.к. Postman в этом конкретном случае ожидет код 400, а не 404
            try {
                final Mpa savedMpa = mpaRepository.getMpaById(mpaId);
                log.debug("Запросили рейтинг фильма из БД. Рейтинг с ID: {} найден: {}", mpaId, savedMpa);
            } catch (MpaNotFoundException e) {
                throw new RequestException(e.getMessage());
            }
        }
        // Проверяем, что указанные в фильме жанры есть в БД
        if (film.getGenres() != null) {
            final List<Integer> genreIds = film.getGenres().stream().map(Genre::getId).toList();
            final List<Genre> savedGenres = genreRepository.getGenresByIds(genreIds);
            log.debug("Запросили жанры фильма из БД для проверки их существования с ID:{} жанры = {}", genreIds, savedGenres);
            if (genreIds.size() != savedGenres.size()) {
                throw new NotFoundException("Жанры c ID: " + genreIds + " не найдены в базе данных");
            }
        }
        return filmRepository.saveFilm(film);
    }


    @Override
    public Film updateFilm(Film film) {
        final Film savedFilm = filmRepository.getFilmById(film.getId());
        log.debug("Проверили, что фильм с таким ID: {} существует в базе данных", film.getId());

        // Проверяем, что указанный в фильме рейтинг есть в БД
        if (film.getMpa() != null) {
            int mpaId = film.getMpa().getId();
            final Mpa savedMpa = mpaRepository.getMpaById(mpaId);
            log.debug("Запросили рейтинг фильма из БД. Рейтинг с ID:{} найден: {}", mpaId, savedMpa);
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

    @Override
    public Collection<Film> getAllFilms() {
        return filmRepository.getAllFilms();
    }

    @Override
    public Film getFilmById(long id) {
        return filmRepository.getFilmById(id);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmRepository.getTopPopular(count);
    }
}
