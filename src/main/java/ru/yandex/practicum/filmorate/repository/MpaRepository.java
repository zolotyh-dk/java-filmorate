package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Optional;

public interface MpaRepository {
    Optional<Mpa> getMpaById(int id);
}
