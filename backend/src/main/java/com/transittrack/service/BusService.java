package com.transittrack.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.transittrack.entity.Bus;
import com.transittrack.repository.BusRepository;
import com.transittrack.repository.RouteRepository;

@Service
public class BusService {

    private final BusRepository busRepository;
    private final RouteRepository routeRepository;

    public BusService(BusRepository busRepository, RouteRepository routeRepository) {
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
    }

    public List<Bus> getAllBuses() { return busRepository.findAll(); }

    public Bus getBusById(Long id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bus not found"));
    }

    private void validate(Bus b) {
        if (b.getBusNumber() == null || b.getBusNumber().isBlank())
            throw new IllegalArgumentException("Bus number is required.");
        if (b.getCapacity() == null || b.getCapacity() < 1)
            throw new IllegalArgumentException("Capacity must be at least 1.");
        if (b.getDriverName() == null || b.getDriverName().isBlank())
            throw new IllegalArgumentException("Driver name is required.");
        String s = b.getStatus() == null ? "ACTIVE" : b.getStatus().toUpperCase();
        b.setStatus(s.equals("INACTIVE") ? "MAINTENANCE" : s);
        if (!b.getStatus().equals("ACTIVE") && !b.getStatus().equals("MAINTENANCE"))
            throw new IllegalArgumentException("Status must be ACTIVE or MAINTENANCE.");
        b.setBusNumber(b.getBusNumber().trim());
    }

    public Bus createBus(Bus bus) {
        validate(bus);
        return busRepository.save(bus);
    }

    public Bus updateBus(Long id, Bus d) {
        validate(d);
        Bus bus = getBusById(id);
        bus.setBusNumber(d.getBusNumber());
        bus.setCapacity(d.getCapacity());
        bus.setDriverName(d.getDriverName());
        bus.setDriverContact(d.getDriverContact());
        bus.setStatus(d.getStatus());
        return busRepository.save(bus);
    }

    public void deleteBus(Long id) {
        Bus bus = getBusById(id);
        if (routeRepository.existsByBus(bus))
            throw new IllegalStateException("This bus is assigned to a route. Reassign or delete that route first.");
        busRepository.delete(bus);
    }
}