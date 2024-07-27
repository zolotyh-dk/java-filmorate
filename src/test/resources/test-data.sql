INSERT INTO mpa (id, name)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

INSERT INTO genres (id, name)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');

INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('Film A', 'Description A', '2024-01-01', 120, 1),
       ('Film B', 'Description B', '2024-02-01', 90, 2),
       ('Film C', 'Description C', '2024-03-01', 150, 3);

INSERT INTO films_genres (film_id, genre_id)
VALUES (1, 1),
       (1, 2),
       (2, 2),
       (3, 3);

INSERT INTO users (email, login, name, birthday)
VALUES ('user1@example.com', 'user1', 'User One', '1990-01-01'),
       ('user2@example.com', 'user2', 'User Two', '1991-02-01'),
       ('user3@example.com', 'user3', 'User Three', '1992-03-01'),
       ('user4@example.com', 'user4', 'User Four', '1992-04-01'),
       ('user5@example.com', 'user5', 'User Five', '1992-05-01');
