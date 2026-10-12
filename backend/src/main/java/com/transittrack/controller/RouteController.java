package com.transittrack.controller;

import com.transittrack.entity.Route;
import com.transittrack.service.RouteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public List<Route> getRoutes(
            @RequestParam(required = false) String stop) {

        if (stop != null && !stop.isBlank()) {
            return routeService.searchByStop(stop);
        }

        return routeService.getAllRoutes();
    }

    @GetMapping("/{id}")
    public Route getRouteById(@PathVariable Long id) {
        return routeService.getRouteById(id);
    }
}
