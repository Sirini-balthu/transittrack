package com.transittrack.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.transittrack.entity.Bus;
import com.transittrack.entity.Route;
import com.transittrack.repository.BusRepository;
import com.transittrack.repository.RegistrationRepository;
import com.transittrack.repository.RouteRepository;

@Service
public class RouteAdminService {

    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final RegistrationRepository registrationRepository;

    public RouteAdminService(RouteRepository routeRepository, BusRepository busRepository,
                             RegistrationRepository registrationRepository) {
        this.routeRepository = routeRepository;
        this.busRepository = busRepository;
        this.registrationRepository = registrationRepository;
    }

    private void validate(Route r) {
        if (r.getRouteName() == null || r.getRouteName().isBlank())
            throw new IllegalArgumentException("Route name is required.");
        if (r.getStops() == null || r.getStops().split("[,;>\\n]+").length < 2)
            throw new IllegalArgumentException("Add at least two stops.");
        if (r.getStartTime() == null || r.getEndTime() == null)
            throw new IllegalArgumentException("Start and end times are required.");
        String s = r.getStatus() == null ? "ON_TIME" : r.getStatus().toUpperCase();
        if (s.equals("ACTIVE")) s = "ON_TIME";
        if (!s.equals("ON_TIME") && !s.equals("DELAYED"))
            throw new IllegalArgumentException("Status must be ON_TIME or DELAYED.");
        r.setStatus(s);
    }

    private Bus bus(Long id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bus not found"));
    }

    public Route createRoute(Route route, Long busId) {
        validate(route);
        route.setBus(bus(busId));
        return routeRepository.save(route);
    }

    public Route updateRoute(Long id, Route d, Long busId) {
        validate(d);
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Route not found"));
        route.setBus(bus(busId));
        route.setRouteName(d.getRouteName());
        route.setStartPoint(d.getStartPoint());
        route.setEndPoint(d.getEndPoint());
        route.setStops(d.getStops());
        route.setStartTime(d.getStartTime());
        route.setEndTime(d.getEndTime());
        route.setStatus(d.getStatus());
        return routeRepository.save(route);
    }

    public Route updateStatus(Long id, String status) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Route not found"));
        route.setStatus(status);
        validate(route);
        return routeRepository.save(route);
    }

    @Transactional
    public void deleteRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Route not found"));
        registrationRepository.deleteByRoute(route);
        routeRepository.delete(route);
    }
}