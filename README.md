# java-filmorate
## Схема базы данных
![Схема базы данных](https://raw.githubusercontent.com/zolotyh-dk/java-filmorate/e9286d2/db_diagram.png)

## Примеры SQL запросов

Список всех фильмов
```sql
SELECT *
FROM films;
```

Список всех пользователей
```sql
SELECT *
FROM users;
```

Топ-10 наиболее популярных фильмов
```sql
SELECT f.id,
       f.name,
       COUNT(l.user_id) AS likes_count
FROM films f
LEFT OUTER JOIN likes l ON f.id = l.film_id
GROUP BY f.id
ORDER BY likes_count DESC
LIMIT 10;
```

Список общих друзей для пользователей с id=200 и id=300 (значения взяты для примера)
```sql
SELECT u.id, u.name  
FROM users u  
INNER JOIN friendship f1 ON u.id = f1.user1_id  
INNER JOIN friendship f2 ON u.id = f2.user2_id  
WHERE f1.user2_id = 200 AND f2.user1_id = 300
```

## DMBL

```dmbl
Table users {
    id integer [primary key]
    email varchar
    login varchar
    name varchar
    birthday date
    created_at timestamp
}

Table films {
    id integer [primary key]
    name varchar
    description varchar
    release_date date
    duration integer
}

Table friendship {
    user1_id integer
    user2_id integer
}

Table likes {
    user_id integer
    film_id integer
}

Ref: users.id < friendship.user1_id

Ref: users.id < friendship.user2_id

Ref: films.id < likes.film_id

Ref: users.id < likes.user_id
```