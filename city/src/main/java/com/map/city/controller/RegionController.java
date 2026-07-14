package com.map.city.controller;

import com.map.city.entity.Region;
import com.map.city.repository.RegionRepository;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionRepository regionRepository;

    public RegionController(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @GetMapping
    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Region getRegionById(@PathVariable Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Region not found with id: " + id));
    }

    @PostMapping
    public Region createRegion(@Valid @RequestBody Region region) {
        return regionRepository.save(region);
    }
    @PutMapping("/{id}")
    public Region updateRegion(@PathVariable Long id, @Valid @RequestBody Region updatedRegion) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Region not found with id: " + id));

        region.setName(updatedRegion.getName());
        region.setDescription(updatedRegion.getDescription());
        region.setParentRegion(updatedRegion.getParentRegion());

        return regionRepository.save(region);
    }

    @DeleteMapping("/{id}")
    public void deleteRegion(@PathVariable Long id) {
        if (!regionRepository.existsById(id)) {
            throw new RuntimeException("Region not found with id: " + id);
        }
        regionRepository.deleteById(id);
    }
}