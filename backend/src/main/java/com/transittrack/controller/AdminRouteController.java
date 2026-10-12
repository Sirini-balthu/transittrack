package com.transittrack.controller;

import com.transittrack.entity.Route;
import com.transittrack.service.RouteAdminService;
import com.transittrack.service.RouteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/routes")
public class AdminRouteController {

    private final RouteAdminService routeAdminService;
    private final RouteService routeService;

    public AdminRouteController(RouteAdminService routeAdminService, RouteService routeService) {
        this.routeAdminService = routeAdminService;
        this.routeService = routeService;
    }

    @GetMapping
    public List<Route> getAllRoutes() { return routeService.getAllRoutes(); }

    @PostMapping
    public Route createRoute(@RequestParam Long busId, @RequestBody Route route) {
        return routeAdminService.createRoute(route, busId);
    }

    @PutMapping("/{id}")
    public Route updateRoute(@PathVariable Long id, @RequestParam Long busId, @RequestBody Route route) {
        return routeAdminService.updateRoute(id, route, busId);
    }

    // Instant schedule update: {"status":"DELAYED"} or {"status":"ON_TIME"}
    @PatchMapping("/{id}/status")
    public Route updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return routeAdminService.updateStatus(id, body.get("status"));
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteRoute(@PathVariable Long id) {
        routeAdminService.deleteRoute(id);
        return Map.of("message", "Route deleted successfully");
    }
}