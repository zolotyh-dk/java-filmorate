package ru.yandex.practicum.filmorate.service.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    public void addLike(long filmId, long userId) {
        userRepository.getUserById(userId); //проверка существования пользователя в хранилище
        filmRepository.addLike(filmId, userId);
        log.info("Пользователь с ID: {} поставил лайк фильму c ID: {}", userId, filmId);
    }

    public void removeLike(long filmId, long userId) {
        userRepository.getUserById(userId); //проверка существования пользователя в хранилище
        filmRepository.removeLike(filmId, userId);
        log.info("Пользователь с ID: {} удалил лайк у фильма c ID: {}", userId, filmId);
    }
}
