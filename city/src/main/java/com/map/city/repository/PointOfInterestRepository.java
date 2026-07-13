package com.map.city.repository;

import com.map.city.entity.PointOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, Long> {
    List<PointOfInterest> findByNameContainingIgnoreCase(String name);
    List<PointOfInterest> findByType(String type);
}