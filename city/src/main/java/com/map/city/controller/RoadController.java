package com.map.city.controller;

import com.map.city.entity.Road;
import com.map.city.repository.RoadRepository;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/roads")
public class RoadController {

    private final RoadRepository roadRepository;

    public RoadController(RoadRepository roadRepository) {
        this.roadRepository = roadRepository;
    }

    @GetMapping
    public List<Road> getAllRoads() {
        return roadRepository.findAll();
    }

    @PostMapping
    public Road createRoad(@Valid @RequestBody Road road) {
        return roadRepository.save(road);
    }
    @PutMapping("/{id}")
    public Road updateRoad(@PathVariable Long id, @Valid @RequestBody Road updatedRoad) {
        Road road = roadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Road not found with id: " + id));

        road.setFromCity(updatedRoad.getFromCity());
        road.setToCity(updatedRoad.getToCity());
        road.setDistance(updatedRoad.getDistance());
        road.setTerrain(updatedRoad.getTerrain());

        return roadRepository.save(road);
    }

    @DeleteMapping("/{id}")
    public void deleteRoad(@PathVariable Long id) {
        if (!roadRepository.existsById(id)) {
            throw new RuntimeException("Road not found with id: " + id);
        }
        roadRepository.deleteById(id);
    }
}