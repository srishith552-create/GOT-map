package com.map.city.service;

import com.map.city.entity.City;
import com.map.city.entity.Road;
import com.map.city.repository.CityRepository;
import com.map.city.repository.RoadRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoutingService {

    private final RoadRepository roadRepository;
    private final CityRepository cityRepository;

    // Miles per day, by terrain type — rough medieval travel norms
    private static final Map<String, Double> TERRAIN_SPEED = Map.of(
            "road", 24.0,
            "kingsroad", 30.0,      // well-maintained major road, faster than average
            "plains", 24.0,
            "forest", 15.0,
            "mountains", 10.0,
            "sea", 100.0            // ships cover far more ground per day than horses
    );
    private static final double DEFAULT_SPEED = 20.0; // fallback if terrain is unrecognized/null

    public RoutingService(RoadRepository roadRepository, CityRepository cityRepository) {
        this.roadRepository = roadRepository;
        this.cityRepository = cityRepository;
    }

    // Straight-line ("as the raven flies") distance — pure geometry, no roads involved
    public double straightLineDistance(Long fromCityId, Long toCityId) {
        City from = cityRepository.findById(fromCityId)
                .orElseThrow(() -> new RuntimeException("City not found: " + fromCityId));
        City to = cityRepository.findById(toCityId)
                .orElseThrow(() -> new RuntimeException("City not found: " + toCityId));

        double dx = from.getX() - to.getX();
        double dy = from.getY() - to.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Shortest real travel distance along roads, using Dijkstra
    public RouteResult shortestPath(Long fromCityId, Long toCityId) {
        List<Road> allRoads = roadRepository.findAll();

        // Build adjacency list: cityId -> list of (neighborCityId, distance)
        Map<Long, List<double[]>> graph = new HashMap<>();
        for (Road road : allRoads) {
            Long a = road.getFromCity().getId();
            Long b = road.getToCity().getId();
            double dist = road.getDistance();

            graph.computeIfAbsent(a, k -> new ArrayList<>()).add(new double[]{b, dist});
            graph.computeIfAbsent(b, k -> new ArrayList<>()).add(new double[]{a, dist}); // roads work both ways
        }

        // Dijkstra setup
        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> previous = new HashMap<>();
        PriorityQueue<double[]> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e[1]));

        distances.put(fromCityId, 0.0);
        pq.add(new double[]{fromCityId, 0.0});

        while (!pq.isEmpty()) {
            double[] current = pq.poll();
            long currentCity = (long) current[0];
            double currentDist = current[1];

            if (currentDist > distances.getOrDefault(currentCity, Double.MAX_VALUE)) continue;
            if (currentCity == toCityId) break; // reached destination

            for (double[] neighbor : graph.getOrDefault(currentCity, Collections.emptyList())) {
                long neighborCity = (long) neighbor[0];
                double edgeWeight = neighbor[1];
                double newDist = currentDist + edgeWeight;

                if (newDist < distances.getOrDefault(neighborCity, Double.MAX_VALUE)) {
                    distances.put(neighborCity, newDist);
                    previous.put(neighborCity, currentCity);
                    pq.add(new double[]{neighborCity, newDist});
                }
            }
        }

        if (!distances.containsKey(toCityId)) {
            throw new RuntimeException("No path found between city " + fromCityId + " and " + toCityId);
        }

        // Reconstruct path by walking backwards through "previous"
        List<Long> path = new ArrayList<>();
        Long step = toCityId;
        while (step != null) {
            path.add(step);
            step = previous.get(step);
        }
        Collections.reverse(path);

        // Walk the path forward and sum up travel days, road by road
        double totalDays = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Long from = path.get(i);
            Long to = path.get(i + 1);
            Road segment = findRoadBetween(allRoads, from, to);
            double speed = TERRAIN_SPEED.getOrDefault(segment.getTerrain(), DEFAULT_SPEED);
            totalDays += segment.getDistance() / speed;
        }

        return new RouteResult(path, distances.get(toCityId), totalDays);
    }

    // Helper: find the specific Road connecting two adjacent cities (either direction)
    private Road findRoadBetween(List<Road> roads, Long cityA, Long cityB) {
        for (Road road : roads) {
            Long a = road.getFromCity().getId();
            Long b = road.getToCity().getId();
            if ((a.equals(cityA) && b.equals(cityB)) || (a.equals(cityB) && b.equals(cityA))) {
                return road;
            }
        }
        throw new RuntimeException("No road found between " + cityA + " and " + cityB);
    }

    // Simple result holder
    public static class RouteResult {
        public List<Long> cityIdPath;
        public double totalDistance;
        public double totalTravelDays;

        public RouteResult(List<Long> cityIdPath, double totalDistance, double totalTravelDays) {
            this.cityIdPath = cityIdPath;
            this.totalDistance = totalDistance;
            this.totalTravelDays = totalTravelDays;
        }
    }
}