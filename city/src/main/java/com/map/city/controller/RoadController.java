package com.map.city.controller;

import com.map.city.entity.Road;
import com.map.city.repository.RoadRepository;
import org.springframework.web.bind.annotation.*;
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
    public Road createRoad(@RequestBody Road road) {
        return roadRepository.save(road);
    }
}