package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import ru.yandex.practicum.filmorate.repository.like.JdbcLikeRepository;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(JdbcLikeRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcLikeRepositoryTest {
    private final JdbcLikeRepository likeRepository;

    @Test
    public void testAddLike() {
        long filmId = 1L;
        long userId = 1L;
        assertThat(isLikeExists(filmId, userId)).isFalse();
        likeRepository.addLike(filmId, userId);
        assertThat(isLikeExists(filmId, userId)).isTrue();
    }

    @Test
    public void testAddLikeDuplicate() {
        long filmId = 1L;
        long userId = 1L;
        likeRepository.addLike(filmId, userId);

        // Попытка добавить тот же лайк снова, ничего не должно измениться
        likeRepository.addLike(filmId, userId);

        // Проверяем, что лайк все еще существует
        assertThat(isLikeExists(filmId, userId)).isTrue();
    }

    @Test
    public void testRemoveLike() {
        long filmId = 1L;
        long userId = 1L;
        likeRepository.addLike(filmId, userId);

        // Проверяем, что лайк существует перед удалением
        assertThat(isLikeExists(filmId, userId)).isTrue();

        likeRepository.removeLike(filmId, userId);

        // Проверяем, что лайк был удален
        assertThat(isLikeExists(filmId, userId)).isFalse();
    }

    // Вспомогательный метод для проверки существования лайка
    private boolean isLikeExists(long filmId, long userId) {
        final String sql = """
                SELECT COUNT(*)
                FROM likes
                WHERE film_id = :filmId AND user_id = :userId
                """;
        var params = new MapSqlParameterSource();
        params.addValue("filmId", filmId);
        params.addValue("userId", userId);
        int count = likeRepository.getJdbc().queryForObject(sql, params, Integer.class);
        return count > 0;
    }
}
