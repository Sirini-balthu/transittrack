package com.transittrack.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transittrack.dto.RegistrationResponse;
import com.transittrack.service.RegistrationService;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    // The student comes from the login session, never from the request body.
    @PostMapping
    public RegistrationResponse create(@RequestBody Map<String, Object> request,
                                       Authentication auth) {
        Object rid = request.get("routeId");
        Object stop = request.get("stopName");
        if (rid == null || stop == null)
            throw new IllegalArgumentException("Choose a route and a boarding stop.");

        return new RegistrationResponse(registrationService.createRegistration(
                auth.getName(), Long.valueOf(rid.toString()), stop.toString()));
    }

    @GetMapping("/my")
    public List<RegistrationResponse> mine(Authentication auth) {
        return registrationService.getMyRegistrations(auth.getName())
                .stream().map(RegistrationResponse::new).toList();
    }

    @DeleteMapping("/{id}")
    public Map<String, String> cancel(@PathVariable Long id, Authentication auth) {
        registrationService.cancelMine(id, auth.getName());
        return Map.of("message", "Request cancelled");
    }
}