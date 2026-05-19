package ru.mephi.vikingdemo.service;

import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.repository.VikingStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VikingAnalyticsService {

    private final VikingStorage storage;
    private final Random random = new Random();

    public VikingAnalyticsService(VikingStorage storage) {
        this.storage = storage;
    }

    public long olderThan(int age) {
        return storage.findAll().stream()
                .filter(v -> v.age() > age)
                .count();
    }

    public long youngerThan(int age) {
        return storage.findAll().stream()
                .filter(v -> v.age() < age)
                .count();
    }

    public long ageInRange(int low, int high) {
        return storage.findAll().stream()
                .filter(v -> v.age() >= low && v.age() <= high)
                .count();
    }

    public long ageOutOfRange(int low, int high) {
        return storage.findAll().stream()
                .filter(v -> v.age() < low || v.age() > high)
                .count();
    }


    public long countByBeardAndHair(BeardStyle beard, HairColor hair) {
        return storage.findAll().stream()
                .filter(v -> v.beardStyle() == beard && v.hairColor() == hair)
                .count();
    }


    public long countWithExactAxes(int expectedCount) {
        return storage.findAll().stream()
                .filter(v -> {
                    long axes = v.equipment().stream()
                            .filter(e -> e.name().toLowerCase().contains("axe"))
                            .count();
                    return axes == expectedCount;
                })
                .count();
    }

    public Optional<Viking> randomTallerThan(int minHeight) {
        List<Viking> tall = storage.findAll().stream()
                .filter(v -> v.heightCm() > minHeight)
                .collect(Collectors.toList());
        return tall.isEmpty() ? Optional.empty() : Optional.of(tall.get(random.nextInt(tall.size())));
    }

    public List<Viking> legendaryArmed() {
        return storage.findAll().stream()
                .filter(v -> v.equipment().stream()
                        .anyMatch(e -> "Legendary".equalsIgnoreCase(e.quality())))
                .collect(Collectors.toList());
    }

    public List<Viking> redHairedSortedByAge() {
        return storage.findAll().stream()
                .filter(v -> v.hairColor() == HairColor.Red)
                .filter(v -> v.beardStyle() != BeardStyle.CLEAN_SHAVEN)
                .sorted(Comparator.comparingInt(Viking::age))
                .collect(Collectors.toList());
    }


    public Optional<Integer> findMaxId() {
        return storage.findAll().stream()
                .map(Viking::id)
                .filter(Objects::nonNull)
                .max(Integer::compareTo);
    }


    public Integer[] collectEvenIds() {
        return storage.findAll().stream()
                .map(Viking::id)
                .filter(id -> id != null && id % 2 == 0)
                .toArray(Integer[]::new);
    }
}

