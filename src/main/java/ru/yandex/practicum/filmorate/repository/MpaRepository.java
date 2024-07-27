package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaRepository {
    Mpa getMpaById(int id);

    Collection<Mpa> getAllMpa();
}
