package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Rating;

public interface RatingRepository {
    Rating getById(int id);
}
