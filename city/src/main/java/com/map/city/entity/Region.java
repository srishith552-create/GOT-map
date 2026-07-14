package com.map.city.entity;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;

@Entity
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Region name is required")
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_region_id")
    private Region parentRegion; // null if top-level (e.g. "The North" has no parent, but "Winterfell's lands" might belong to it)

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Region getParentRegion() { return parentRegion; }
    public void setParentRegion(Region parentRegion) { this.parentRegion = parentRegion; }
}