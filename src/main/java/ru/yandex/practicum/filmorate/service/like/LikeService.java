package ru.yandex.practicum.filmorate.service.like;

public interface LikeService {
    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);
}
