package com.map.city.controller;

import com.map.city.entity.City;
import com.map.city.repository.CityRepository;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityRepository cityRepository;

    public CityController(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @GetMapping
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    @GetMapping("/{id}")
    public City getCityById(@PathVariable Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found with id: " + id));
    }

    @PostMapping
    public City createCity(@Valid @RequestBody City city) {
        return cityRepository.save(city);
    }
    @PutMapping("/{id}")
    public City updateCity(@PathVariable Long id, @Valid @RequestBody City updatedCity) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found with id: " + id));

        city.setName(updatedCity.getName());
        city.setX(updatedCity.getX());
        city.setY(updatedCity.getY());
        city.setPopulation(updatedCity.getPopulation());
        city.setRegion(updatedCity.getRegion());

        return cityRepository.save(city);
    }

    @DeleteMapping("/{id}")
    public void deleteCity(@PathVariable Long id) {
        if (!cityRepository.existsById(id)) {
            throw new RuntimeException("City not found with id: " + id);
        }
        cityRepository.deleteById(id);
    }
    
}
