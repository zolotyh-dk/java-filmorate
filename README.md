# java-filmorate
## Схема базы данных
![Схема базы данных](https://raw.githubusercontent.com/zolotyh-dk/java-filmorate/cf2765e/java-filmprate-er-diagram.png)

## DMBL

```dmbl
Table users {
    id bigint [primary key]
    email varchar
    login varchar
    name varchar
    birthday date
}

Table films {
    id bigint [primary key]
    name varchar
    description varchar
    release_date date
    duration integer
    rating_id integer
}

Table friendship {
    user_id bigint
    friend_id bigint
}

Table likes {
    user_id bigint
    film_id bigint
}

Table generes {
    id integer
    genre varchar
}

Table ratings {
    id integer
    ratings varchar
}

Table films_genres {
    film_id bigint
    genre_id integer
}

Ref: users.id < friendship.user_id

Ref: users.id < friendship.friend_id

Ref: films.id < likes.film_id

Ref: users.id < likes.user_id

Ref: films.rating_id < ratings.id

Ref: films.id < films_genres.film_id

Ref: generes.id < films_genres.genre_id
```