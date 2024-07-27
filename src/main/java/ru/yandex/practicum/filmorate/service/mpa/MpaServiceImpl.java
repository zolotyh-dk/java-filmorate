package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaRepository mpaRepository;

    @Override
    public Collection<Mpa> getAllMpa() {
        return mpaRepository.getAllMpa();
    }

    @Override
    public Mpa getMpaById(int id) {
        return mpaRepository.getMpaById(id);
    }
}
