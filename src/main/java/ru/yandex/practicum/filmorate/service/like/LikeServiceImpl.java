package ru.yandex.practicum.filmorate.service.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.repository.like.LikeRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    public void addLike(long filmId, long userId) {
        userRepository.getUserById(userId); //проверка существования пользователя в хранилище
        likeRepository.addLike(filmId, userId);
        log.info("Пользователь с ID: {} поставил лайк фильму c ID: {}", userId, filmId);
    }

    public void removeLike(long filmId, long userId) {
        userRepository.getUserById(userId); //проверка существования пользователя в хранилище
        likeRepository.removeLike(filmId, userId);
        log.info("Пользователь с ID: {} удалил лайк у фильма c ID: {}", userId, filmId);
    }
}
