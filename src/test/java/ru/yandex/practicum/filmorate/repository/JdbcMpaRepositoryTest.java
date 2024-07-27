package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mpa.JdbcMpaRepository;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Import(JdbcMpaRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcMpaRepositoryTest {
    private final JdbcMpaRepository mpaRepository;

    @Test
    public void testGetMpaById() {
        Mpa mpa = mpaRepository.getMpaById(1);

        assertThat(mpa).isNotNull();
        assertThat(mpa.getId()).isEqualTo(1);
        assertThat(mpa.getName()).isEqualTo("G");
    }

    @Test
    public void testGetMpaByIdNotFound() {
        assertThatThrownBy(() -> mpaRepository.getMpaById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Рейтинг с ID: 999 не найден в базе данных");
    }

    @Test
    public void testGetAllMpa() {
        Collection<Mpa> mpas = mpaRepository.getAllMpa();

        assertThat(mpas).isNotNull().hasSize(5);

        Mpa mpa = mpas.stream().filter(m -> m.getId() == 1).findFirst().orElse(null);
        assertThat(mpa).isNotNull();
        assertThat(mpa.getName()).isEqualTo("G");
    }
}
