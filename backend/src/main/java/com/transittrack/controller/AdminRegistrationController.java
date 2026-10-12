
package com.transittrack.controller;

import com.transittrack.dto.RegistrationResponse;
import com.transittrack.service.RegistrationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/registrations")
public class AdminRegistrationController {

    private final RegistrationService registrationService;

    public AdminRegistrationController(
            RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public List<RegistrationResponse> getAllRegistrations() {
        return registrationService.getAllRegistrations()
                .stream()
                .map(RegistrationResponse::new)
                .toList();
    }

    @PutMapping("/{id}/approve")
    public RegistrationResponse approveRegistration(
            @PathVariable Long id) {
        return new RegistrationResponse(
                registrationService.approveRegistration(id));
    }

    @PutMapping("/{id}/reject")
    public RegistrationResponse rejectRegistration(
            @PathVariable Long id) {
        return new RegistrationResponse(
                registrationService.rejectRegistration(id));
    }
}
