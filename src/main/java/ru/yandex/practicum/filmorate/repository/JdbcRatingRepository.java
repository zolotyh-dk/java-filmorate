package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.RatingRowMapper;
import ru.yandex.practicum.filmorate.model.Rating;

@Repository
@RequiredArgsConstructor
public class JdbcRatingRepository implements RatingRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Rating getById(int id) {
        String sql = "SELECT * FROM ratings " +
                     "WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ratingId", id);
        RowMapper<Rating> rowMapper = new RatingRowMapper();
        return jdbc.queryForObject(sql, params, rowMapper);
    }
}
