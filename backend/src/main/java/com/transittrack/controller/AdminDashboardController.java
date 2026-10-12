package com.transittrack.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transittrack.repository.BusRepository;
import com.transittrack.repository.RegistrationRepository;
import com.transittrack.repository.RouteRepository;
import com.transittrack.repository.UserRepository;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final BusRepository buses;
    private final RouteRepository routes;
    private final UserRepository users;
    private final RegistrationRepository regs;

    public AdminDashboardController(BusRepository buses, RouteRepository routes,
                                    UserRepository users, RegistrationRepository regs) {
        this.buses = buses; this.routes = routes; this.users = users; this.regs = regs;
    }

    @GetMapping
    public Map<String, Object> dashboard() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalBuses", buses.count());
        m.put("totalRoutes", routes.count());
        m.put("delayedRoutes", routes.countByStatus("DELAYED"));
        m.put("totalStudents", users.countByRole("STUDENT"));
        m.put("pendingRegistrations", regs.countByStatus("PENDING"));
        m.put("approvedRegistrations", regs.countByStatus("APPROVED"));
        m.put("totalRegistrations", regs.count());
        return m;
    }
}