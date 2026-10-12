package com.transittrack.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.transittrack.entity.Registration;
import com.transittrack.entity.Route;
import com.transittrack.entity.User;
import com.transittrack.repository.RegistrationRepository;
import com.transittrack.repository.RouteRepository;
import com.transittrack.repository.UserRepository;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;

    public RegistrationService(RegistrationRepository registrationRepository,
                               UserRepository userRepository,
                               RouteRepository routeRepository) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
    }

    private User userByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    public Registration createRegistration(String email, Long routeId, String stopName) {
        if (routeId == null || stopName == null || stopName.isBlank())
            throw new IllegalArgumentException("Choose a route and a boarding stop.");

        User user = userByEmail(email);
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new NoSuchElementException("Route not found"));

        boolean validStop = Arrays.stream(String.valueOf(route.getStops()).split("[,;>\\n]+"))
                .map(String::trim).anyMatch(s -> s.equalsIgnoreCase(stopName.trim()));
        if (!validStop)
            throw new IllegalArgumentException("That stop is not on this route.");

        if (!"ACTIVE".equalsIgnoreCase(route.getBus().getStatus()))
            throw new IllegalStateException("This route's bus is not in service right now.");

        if (registrationRepository.existsByUserAndRouteAndStatusIn(
                user, route, Set.of("PENDING", "APPROVED")))
            throw new IllegalStateException("You already have a request for this route.");

        Registration r = new Registration();
        r.setUser(user);
        r.setRoute(route);
        r.setStopName(stopName.trim());
        r.setStatus("PENDING");
        r.setRequestedAt(LocalDateTime.now());
        return registrationRepository.save(r);
    }

    public List<Registration> getMyRegistrations(String email) {
        return registrationRepository.findByUser(userByEmail(email));
    }

    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    @Transactional
    public Registration approveRegistration(Long id) {
        Registration r = find(id);
        if (!"PENDING".equals(r.getStatus()))
            throw new IllegalStateException("Only pending requests can be approved.");

        int capacity = r.getRoute().getBus().getCapacity() == null ? 0 : r.getRoute().getBus().getCapacity();
        long taken = registrationRepository.countByRouteAndStatus(r.getRoute(), "APPROVED");
        if (taken >= capacity)
            throw new IllegalStateException("No seats left on this bus.");

        r.setStatus("APPROVED");
        return registrationRepository.save(r);
    }

    @Transactional
    public Registration rejectRegistration(Long id) {
        Registration r = find(id);
        if (!"PENDING".equals(r.getStatus()))
            throw new IllegalStateException("Only pending requests can be rejected.");
        r.setStatus("REJECTED");
        return registrationRepository.save(r);
    }

    public void cancelMine(Long id, String email) {
        Registration r = find(id);
        if (!r.getUser().getEmail().equalsIgnoreCase(email))
            throw new NoSuchElementException("Registration not found");
        if (!"PENDING".equals(r.getStatus()))
            throw new IllegalStateException("Only pending requests can be cancelled.");
        registrationRepository.delete(r);
    }

    private Registration find(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Registration not found"));
    }
}