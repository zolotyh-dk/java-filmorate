package ru.yandex.practicum.filmorate.repository.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaRepository {
    Mpa getMpaById(int id);

    Collection<Mpa> getAllMpa();
}
