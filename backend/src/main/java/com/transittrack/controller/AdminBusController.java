
package com.transittrack.controller;

import com.transittrack.entity.Bus;
import com.transittrack.service.BusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/buses")
public class AdminBusController {

    private final BusService busService;

    public AdminBusController(BusService busService) {
        this.busService = busService;
    }

    @GetMapping
    public List<Bus> getAllBuses() {
        return busService.getAllBuses();
    }

    @GetMapping("/{id}")
    public Bus getBusById(@PathVariable Long id) {
        return busService.getBusById(id);
    }

    @PostMapping
    public Bus createBus(@RequestBody Bus bus) {
        return busService.createBus(bus);
    }

    @PutMapping("/{id}")
    public Bus updateBus(
            @PathVariable Long id,
            @RequestBody Bus bus) {
        return busService.updateBus(id, bus);
    }

    @DeleteMapping("/{id}")
    public String deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return "Bus deleted successfully";
    }
}
