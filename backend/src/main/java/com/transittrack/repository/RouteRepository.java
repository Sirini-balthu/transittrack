package com.transittrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transittrack.entity.Bus;
import com.transittrack.entity.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByStopsContainingIgnoreCase(String stop);

    boolean existsByBus(Bus bus);

    long countByStatus(String status);
}