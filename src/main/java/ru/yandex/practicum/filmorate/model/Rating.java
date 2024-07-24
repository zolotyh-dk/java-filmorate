package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Rating {
    private int id;
    private Mpa mpa;

    private enum Mpa {
        G, PG, PG_13, R, NC_17
    }
}
