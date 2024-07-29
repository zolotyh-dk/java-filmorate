package ru.yandex.practicum.filmorate.repository.like;

public interface LikeRepository {
    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);
}
