package com.map.city.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "City name is required")
    private String name;

    private double x;
    private double y;

    @Positive(message = "Population must be a positive number")
    private Integer population;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public Integer getPopulation() { return population; }
    public void setPopulation(Integer population) { this.population = population; }
    public Region getRegion() { return region; }
    public void setRegion(Region region) { this.region = region; }
}