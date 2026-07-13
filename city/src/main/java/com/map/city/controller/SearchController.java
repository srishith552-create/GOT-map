package com.map.city.controller;

import com.map.city.entity.City;
import com.map.city.entity.Region;
import com.map.city.entity.PointOfInterest;
import com.map.city.repository.CityRepository;
import com.map.city.repository.RegionRepository;
import com.map.city.repository.PointOfInterestRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;
    private final PointOfInterestRepository pointOfInterestRepository;

    public SearchController(CityRepository cityRepository,
                             RegionRepository regionRepository,
                             PointOfInterestRepository pointOfInterestRepository) {
        this.cityRepository = cityRepository;
        this.regionRepository = regionRepository;
        this.pointOfInterestRepository = pointOfInterestRepository;
    }

    
    @GetMapping
    public Map<String, Object> searchAll(@RequestParam String q) {
        List<City> cities = cityRepository.findByNameContainingIgnoreCase(q);
        List<Region> regions = regionRepository.findByNameContainingIgnoreCase(q);
        List<PointOfInterest> pointsOfInterest = pointOfInterestRepository.findByNameContainingIgnoreCase(q);

        return Map.of(
                "cities", cities,
                "regions", regions,
                "pointsOfInterest", pointsOfInterest
        );
    }

   
    @GetMapping("/cities")
    public List<City> searchCities(@RequestParam String q) {
        return cityRepository.findByNameContainingIgnoreCase(q);
    }

   
    @GetMapping("/regions")
    public List<Region> searchRegions(@RequestParam String q) {
        return regionRepository.findByNameContainingIgnoreCase(q);
    }

    
    @GetMapping("/points-of-interest")
    public List<PointOfInterest> searchPointsOfInterest(@RequestParam String q) {
        return pointOfInterestRepository.findByNameContainingIgnoreCase(q);
    }
}