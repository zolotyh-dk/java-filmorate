package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.genre.JdbcGenreRepository;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Import(JdbcGenreRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcGenreRepositoryTest {
    private final JdbcGenreRepository genreRepository;

    @Test
    public void testGetGenresByIds() {
        List<Integer> genreIds = List.of(1, 2);
        List<Genre> genres = genreRepository.getGenresByIds(genreIds);

        assertThat(genres).isNotNull().hasSize(2);

        Genre genre1 = genres.stream().filter(g -> g.getId() == 1).findFirst().orElse(null);
        Genre genre2 = genres.stream().filter(g -> g.getId() == 2).findFirst().orElse(null);

        assertThat(genre1).isNotNull();
        assertThat(genre1.getId()).isEqualTo(1);
        assertThat(genre1.getName()).isEqualTo("Комедия");

        assertThat(genre2).isNotNull();
        assertThat(genre2.getId()).isEqualTo(2);
        assertThat(genre2.getName()).isEqualTo("Драма");
    }

    @Test
    public void testGetAllGenres() {
        Collection<Genre> genres = genreRepository.getAllGenres();

        assertThat(genres).isNotNull().hasSize(6);

        Genre genre = genres.stream().filter(g -> g.getId() == 1).findFirst().orElse(null);
        assertThat(genre).isNotNull();
        assertThat(genre.getName()).isEqualTo("Комедия");
    }

    @Test
    public void testGetGenreById() {
        Genre genre = genreRepository.getGenreById(1);

        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1);
        assertThat(genre.getName()).isEqualTo("Комедия");
    }

    @Test
    public void testGetGenreByIdNotFound() {
        assertThatThrownBy(() -> genreRepository.getGenreById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Жанр с ID 999 не найден в базе данных");
    }
}
