package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.film.JdbcFilmRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(JdbcFilmRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcFilmRepositoryTest {
    private final JdbcFilmRepository filmRepository;

    @Test
    public void testSaveFilm() {
        Film film = new Film();
        film.setName("New Test Film");
        film.setDescription("New Test Description");
        film.setReleaseDate(LocalDate.of(2024, 7, 27));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film.setMpa(mpa);
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        film.setGenres(Collections.singleton(genre));

        Film savedFilm = filmRepository.saveFilm(film);
        Film filmFromDb = filmRepository.getFilmById(savedFilm.getId());

        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm.getId()).isEqualTo(filmFromDb.getId());
        assertThat(filmFromDb).usingRecursiveComparison().isEqualTo(film);
    }

    @Test
    public void testUpdateFilm() {
        Film film = filmRepository.getFilmById(1L);

        film.setName("Updated Film");
        film.setDescription("Updated Description");
        film.setDuration(130);

        Film updatedFilm = filmRepository.updateFilm(film);
        Film filmFromDb = filmRepository.getFilmById(film.getId());

        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm).isEqualTo(filmFromDb);
        assertThat(updatedFilm.getName()).isEqualTo(film.getName());
        assertThat(updatedFilm.getDescription()).isEqualTo(film.getDescription());
        assertThat(updatedFilm.getDuration()).isEqualTo(film.getDuration());
    }

    @Test
    public void testGetFilmById() {
        Film expectedFilm = filmRepository.getFilmById(2L);

        assertThat(expectedFilm).isNotNull();
        assertThat(expectedFilm.getId()).isEqualTo(2L);
        assertThat(expectedFilm.getName()).isEqualTo("Film B");
        assertThat(expectedFilm.getDescription()).isEqualTo("Description B");
        assertThat(expectedFilm.getReleaseDate()).isEqualTo(LocalDate.of(2024, 2, 1));
        assertThat(expectedFilm.getDuration()).isEqualTo(90);
    }

    @Test
    public void testGetAllFilms() {
        List<Film> filmsFromDb = new ArrayList<>(filmRepository.getAllFilms());

        assertThat(filmsFromDb).isNotNull().hasSize(3); // Проверяем, что в базе данных три фильма
        assertThat(filmsFromDb).extracting(Film::getName).containsExactlyInAnyOrder("Film A", "Film B", "Film C");
    }

    @Test
    public void testGetTopPopular() {
        List<Film> topPopularFilms = filmRepository.getTopPopular(2);
        assertThat(topPopularFilms).isNotNull().hasSize(2);
    }
}
