package com.map.city.repository;

import com.map.city.entity.Road;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoadRepository extends JpaRepository<Road, Long> {
    List<Road> findByFromCityIdOrToCityId(Long fromCityId, Long toCityId);
}