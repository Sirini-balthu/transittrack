package com.transittrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transittrack.entity.Bus;
import com.transittrack.repository.BusRepository;

/** Read-only bus list for logged-in students (the /admin/buses endpoint is admin-only). */
@RestController
@RequestMapping("/buses")
public class PublicBusController {

    private final BusRepository busRepository;

    public PublicBusController(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    @GetMapping
    public List<Bus> all() {
        return busRepository.findAll();
    }
}