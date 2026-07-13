package com.map.city.entity;

import jakarta.persistence.*;

@Entity
public class Road {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_city_id")
    private City fromCity;

    @ManyToOne
    @JoinColumn(name = "to_city_id")
    private City toCity;

    private double distance; 
    private String terrain;  

    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public City getFromCity() { return fromCity; }
    public void setFromCity(City fromCity) { this.fromCity = fromCity; }

    public City getToCity() { return toCity; }
    public void setToCity(City toCity) { this.toCity = toCity; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public String getTerrain() { return terrain; }
    public void setTerrain(String terrain) { this.terrain = terrain; }
}