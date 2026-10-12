package com.transittrack.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.transittrack.entity.Bus;
import com.transittrack.entity.Route;
import com.transittrack.entity.User;
import com.transittrack.repository.BusRepository;
import com.transittrack.repository.RouteRepository;
import com.transittrack.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final BusRepository buses;
    private final RouteRepository routes;
    private final PasswordEncoder encoder;
    private final String adminEmail;
    private final String adminPassword;

    public DataSeeder(UserRepository users, BusRepository buses, RouteRepository routes,
                      PasswordEncoder encoder,
                      @Value("${transittrack.admin.email}") String adminEmail,
                      @Value("${transittrack.admin.password}") String adminPassword) {
        this.users = users; this.buses = buses; this.routes = routes; this.encoder = encoder;
        this.adminEmail = adminEmail; this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (users.findByEmail(adminEmail).isEmpty()) {
            User a = new User();
            a.setName("Transport Admin");
            a.setEmail(adminEmail);
            a.setPasswordHash(encoder.encode(adminPassword));
            a.setRole("ADMIN");
            users.save(a);
            System.out.println("Admin created: " + adminEmail);
        }
        if (buses.count() == 0) {
            Bus b1 = bus("TS-09-UA-1201", 50, "Ramesh Kumar", "9876500011");
            Bus b2 = bus("TS-09-UB-3344", 40, "Suresh Reddy", "9876500022");
            route(b1, "Route A - Hayathnagar", "Hayathnagar", "College Campus", "07:15", "08:45",
                  "Hayathnagar, Pedda Amberpet, Hastinapuram, Vanasthalipuram, College Campus", "ON_TIME");
            route(b2, "Route B - L.B. Nagar", "L.B. Nagar", "College Campus", "07:00", "08:50",
                  "L.B. Nagar, Dilsukhnagar, Kothapet, Nagole, College Campus", "DELAYED");
        }
    }

    private Bus bus(String n, int c, String d, String t) {
        Bus b = new Bus();
        b.setBusNumber(n); b.setCapacity(c); b.setDriverName(d); b.setDriverContact(t); b.setStatus("ACTIVE");
        return buses.save(b);
    }

    private void route(Bus bus, String n, String sp, String ep, String st, String et, String stops, String status) {
        Route r = new Route();
        r.setBus(bus); r.setRouteName(n); r.setStartPoint(sp); r.setEndPoint(ep);
        r.setStartTime(st); r.setEndTime(et); r.setStops(stops); r.setStatus(status);
        routes.save(r);
    }
}