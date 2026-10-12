
package com.transittrack.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController

public class SimulatedBusController {

    private static class DemoBus {
        private final String busNumber;
        private final String route;
        private double latitude;
        private double longitude;
        private double latitudeStep;
        private double longitudeStep;

        public DemoBus(
                String busNumber,
                String route,
                double latitude,
                double longitude,
                double latitudeStep,
                double longitudeStep) {

            this.busNumber = busNumber;
            this.route = route;
            this.latitude = latitude;
            this.longitude = longitude;
            this.latitudeStep = latitudeStep;
            this.longitudeStep = longitudeStep;
        }
    }

    private final List<DemoBus> buses = new ArrayList<>();

    public SimulatedBusController() {
        buses.add(new DemoBus(
                "DEMO01",
                "Route A - Hayathnagar",
                17.4300, 78.5900,
                0.00008, 0.00006
        ));

        buses.add(new DemoBus(
                "DEMO02",
                "Route B - L.B. Nagar",
                17.4100, 78.5700,
                0.00005, 0.00008
        ));

        buses.add(new DemoBus(
                "DEMO03",
                "Route C - Peddamberpet",
                17.4500, 78.6100,
                -0.00006, 0.00007
        ));
    }

    @GetMapping("/api/simulated-buses")
    public synchronized List<Map<String, Object>> getSimulatedBuses() {

        List<Map<String, Object>> result = new ArrayList<>();

        for (DemoBus bus : buses) {

            bus.latitude += bus.latitudeStep;
            bus.longitude += bus.longitudeStep;

            if (bus.latitude >= 17.47 || bus.latitude <= 17.39) {
                bus.latitudeStep = -bus.latitudeStep;
            }

            if (bus.longitude >= 78.63 || bus.longitude <= 78.55) {
                bus.longitudeStep = -bus.longitudeStep;
            }

            Map<String, Object> item = new LinkedHashMap<>();

            item.put("busNumber", bus.busNumber);
            item.put("route", bus.route);
            item.put("latitude", bus.latitude);
            item.put("longitude", bus.longitude);
            item.put("simulated", true);

            result.add(item);
        }

        return result;
    }
}
