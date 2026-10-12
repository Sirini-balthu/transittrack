package com.transittrack.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transittrack.entity.Registration;
import com.transittrack.entity.Route;
import com.transittrack.entity.User;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findByUser(User user);

    boolean existsByUserAndRouteAndStatusIn(User user, Route route, Collection<String> statuses);

    long countByRouteAndStatus(Route route, String status);

    long countByStatus(String status);

    void deleteByRoute(Route route);
}