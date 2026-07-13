package com.map.city.entity;

import jakarta.persistence.*;

@Entity
public class PointOfInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // "landmark", "castle", "ruin", "temple", etc.
    private double x;
    private double y;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public Region getRegion() { return region; }
    public void setRegion(Region region) { this.region = region; }
}