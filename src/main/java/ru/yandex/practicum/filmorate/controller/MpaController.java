package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
@Slf4j
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> getAllMpa() {
        log.info("Получен запрос на получение всех рейтингов. GET /mpa");
        final Collection<Mpa> allMpa = mpaService.getAllMpa();
        log.info("Возвращаем все рейтинги. GET /mpa c телом {}", allMpa);
        return allMpa;
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        log.info("Получен запрос на получение рейтинга по ID. GET /mpa/{}", id);
        final Mpa mpa = mpaService.getMpaById(id);
        log.info("Возвращаем рейтинг. GET /mpa/{} c телом: {}", id, mpa);
        return mpa;
    }
}
