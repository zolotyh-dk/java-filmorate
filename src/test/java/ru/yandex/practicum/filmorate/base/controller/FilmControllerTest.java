package ru.yandex.practicum.filmorate.base.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.base.exception.ValidationException;
import ru.yandex.practicum.filmorate.base.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    public void setup() {
        filmController = new FilmController();
    }

    @Test
    public void testSaveFilmValidFilm() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2024, 6, 1));
        film.setDuration(120);

        Film savedFilm = filmController.saveFilm(film);

        assertNotNull(savedFilm);
        assertEquals(film.getName(), savedFilm.getName());
        assertEquals(film.getDescription(), savedFilm.getDescription());
        assertEquals(film.getReleaseDate(), savedFilm.getReleaseDate());
        assertEquals(film.getDuration(), savedFilm.getDuration());
    }

    @Test
    public void testSaveFilmBlankName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2024, 6, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.saveFilm(film));
    }

    @Test
    public void testSaveFilmLongDescription() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание".repeat(30));
        film.setReleaseDate(LocalDate.of(2024, 6, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.saveFilm(film));
    }

    @Test
    public void testSaveFilmReleaseDateBeforeMinDate() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.saveFilm(film));
    }

    @Test
    public void testSaveFilmNegativeDuration() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2024, 6, 1));
        film.setDuration(-120);

        assertThrows(ValidationException.class, () -> filmController.saveFilm(film));
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2024, 6, 1));
        film.setDuration(120);
        Film savedFilm = filmController.saveFilm(film);

        savedFilm.setDescription("Обновленное описание");
        savedFilm.setDuration(150);
        Film updatedFilm = filmController.updateFilm(savedFilm);

        assertNotNull(updatedFilm);
        assertEquals(savedFilm.getId(), updatedFilm.getId());
        assertEquals(savedFilm.getName(), updatedFilm.getName());
        assertEquals(savedFilm.getDescription(), updatedFilm.getDescription());
        assertEquals(savedFilm.getReleaseDate(), updatedFilm.getReleaseDate());
        assertEquals(savedFilm.getDuration(), updatedFilm.getDuration());
    }

    @Test
    public void testUpdateFilmNotFound() {
        Film film = new Film();
        film.setId(999);
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2024, 6, 1));
        film.setDuration(120);

        assertThrows(NoSuchElementException.class, () -> filmController.updateFilm(film));
    }

    @Test
    public void testGetAllFilms() {
        Collection<Film> films = filmController.getAllFilms();
        assertTrue(films.isEmpty());

        Film film1 = new Film();
        film1.setName("Фильм 1");
        film1.setDescription("Описание 1");
        film1.setReleaseDate(LocalDate.of(2024, 6, 1));
        film1.setDuration(120);
        filmController.saveFilm(film1);

        Film film2 = new Film();
        film2.setName("Фильм 2");
        film2.setDescription("Описание 2");
        film2.setReleaseDate(LocalDate.of(2024, 6, 2));
        film2.setDuration(130);
        filmController.saveFilm(film2);

        films = filmController.getAllFilms();
        assertEquals(2, films.size());
        assertTrue(films.contains(film1));
        assertTrue(films.contains(film2));
    }
}