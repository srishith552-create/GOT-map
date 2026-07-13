package com.map.city.controller;

import com.map.city.entity.PointOfInterest;
import com.map.city.repository.PointOfInterestRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/points-of-interest")
public class PointOfInterestController {

    private final PointOfInterestRepository pointOfInterestRepository;

    public PointOfInterestController(PointOfInterestRepository pointOfInterestRepository) {
        this.pointOfInterestRepository = pointOfInterestRepository;
    }

    @GetMapping
    public List<PointOfInterest> getAllPointsOfInterest() {
        return pointOfInterestRepository.findAll();
    }

    @GetMapping("/{id}")
    public PointOfInterest getPointOfInterestById(@PathVariable Long id) {
        return pointOfInterestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Point of interest not found with id: " + id));
    }

    @PostMapping
    public PointOfInterest createPointOfInterest(@RequestBody PointOfInterest poi) {
        return pointOfInterestRepository.save(poi);
    }
    @PutMapping("/{id}")
    public PointOfInterest updatePointOfInterest(@PathVariable Long id, @RequestBody PointOfInterest updatedPoi) {
        PointOfInterest poi = pointOfInterestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Point of interest not found with id: " + id));

        poi.setName(updatedPoi.getName());
        poi.setType(updatedPoi.getType());
        poi.setX(updatedPoi.getX());
        poi.setY(updatedPoi.getY());
        poi.setRegion(updatedPoi.getRegion());

        return pointOfInterestRepository.save(poi);
    }

    @DeleteMapping("/{id}")
    public void deletePointOfInterest(@PathVariable Long id) {
        if (!pointOfInterestRepository.existsById(id)) {
            throw new RuntimeException("Point of interest not found with id: " + id);
        }
        pointOfInterestRepository.deleteById(id);
    }
}