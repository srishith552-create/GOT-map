package com.map.city.controller;

import com.map.city.service.RoutingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routes")
public class RoutingController {

    private final RoutingService routingService;

    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @GetMapping("/shortest")
    public RoutingService.RouteResult getShortestPath(
            @RequestParam Long from,
            @RequestParam Long to) {
        return routingService.shortestPath(from, to);
    }

    @GetMapping("/straight-line")
    public double getStraightLineDistance(
            @RequestParam Long from,
            @RequestParam Long to) {
        return routingService.straightLineDistance(from, to);
    }
}