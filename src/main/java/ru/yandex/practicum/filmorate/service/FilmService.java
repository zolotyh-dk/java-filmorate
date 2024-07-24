package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
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
//    private final GenreRepository genreRepository;

    @Autowired
    public FilmService(@Qualifier("jdbcFilmRepository") FilmRepository filmRepository,
                       UserRepository userRepository)
//            , GenreRepository genreRepository)
                       {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
//        this.genreRepository = genreRepository;
    }

    public Film saveFilm(Film film) {
        //Валидируем фильм относительно БД
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()){

            }
        }
        return filmRepository.saveFilm(film);
    }

    public Film updateFilm(Film film) {
        //Получить фильм по id
        final Film f = filmRepository.getFilmById(film.getId()); //можно возвращать optional и распаковывать с orElseThrow
        //получить рейтинг по id
//        final Rating r = ratingRepository.getById(film.getRating().getId()) // получили рейтинг. Если его нет выбросили исключение
        //Получить жанры по списку id
//        final List<Long> genreIds = film.getGenres().stream().map(Genre::getId).toList();
//        final List<Genre> genres = genreRepository.getByIds(genreIds); //в запросе используем IN
//        if (genreIds.size() != genres.size()) {
//            throw new NotFoundException("Жанры не найдены");
//        }

//        f.setRating(r);
//        f.setGenres(new HashSet<>(genres));
        f.setName(film.getName());
        //сет остальных полей
        filmRepository.updateFilm(f);
        //Дальше в репозитории
        //Обновить фильм и его рейтинг UPDATE
        //Удаляем связи фильмы - жанры DELETE т.к мы не знаем какие были жанры до обновления
        //Добавить связи фильмы - жанры через batch jdbc INSERT чтобы объединить несколько запросов
        return null;
    }

    public Collection<Film> getAllFilms() {
        return filmRepository.getAllFilms();
    }

    public Film getFilmById(long id) {
//        final Film film = filmRepository.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с id..."))
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
