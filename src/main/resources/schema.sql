DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS friendship;
DROP TABLE IF EXISTS films_genres;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS mpa;

CREATE TABLE mpa (
    id          INTEGER PRIMARY KEY,
    name        VARCHAR(5) NOT NULL
);

CREATE TABLE films (
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(50) NOT NULL,
    description  VARCHAR(200),
    release_date DATE NOT NULL,
    duration     INTEGER NOT NULL,
    mpa_id    INTEGER,
    CONSTRAINT films_ratings_rating_id_fk FOREIGN KEY (mpa_id) REFERENCES mpa
);

CREATE TABLE genres (
    id          INTEGER PRIMARY KEY,
    name        VARCHAR(50) NOT NULL
);

CREATE TABLE films_genres (
    film_id     BIGINT NOT NULL,
    genre_id    INTEGER NOT NULL,
    CONSTRAINT films_genres_film_id_fk FOREIGN KEY (film_id) REFERENCES films,
    CONSTRAINT films_genres_genre_id_fk FOREIGN KEY (genre_id) REFERENCES genres
);

CREATE TABLE users (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email       VARCHAR(255) NOT NULL,
    login       VARCHAR(50) NOT NULL,
    name        VARCHAR(50) NOT NULL,
    birthday    DATE NOT NULL
);

CREATE TABLE likes (
    user_id     BIGINT NOT NULL,
    film_id     BIGINT NOT NULL,
    CONSTRAINT likes_user_id_fk FOREIGN KEY (user_id) REFERENCES users,
    CONSTRAINT likes_film_id_fk FOREIGN KEY (film_id) REFERENCES films
);

CREATE table friendship (
    user_id    BIGINT NOT NULL,
    friend_id  BIGINT NOT NULL,
    CONSTRAINT friendship_user_id FOREIGN KEY (user_id) REFERENCES users,
    CONSTRAINT friendship_friend_id FOREIGN KEY (friend_id) REFERENCES users
);