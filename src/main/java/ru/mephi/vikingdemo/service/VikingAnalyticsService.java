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


   public long countWithExactAxes() {
        return storage.findAll().stream()
                .filter(v -> {
                    long axes = v.equipment().stream()
                            .filter(e -> e.name().toLowerCase().contains("axe"))
                            .count();
                    return axes == 1 || axes==2;
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


   public int findMaxId() {
        int[] ids = vikingStorage.findAll().stream()
                .mapToInt(Viking::id)
                .toArray();
        return Arrays.stream(ids).max().orElse(-1);
    }


    
    public int[] collectEvenIds() {
        int[] ids = vikingStorage.findAll().stream()
                .mapToInt(Viking::id)
                .toArray();
        return Arrays.stream(ids)
                .filter(id -> id % 2 == 0)
                .toArray();
    }
}


