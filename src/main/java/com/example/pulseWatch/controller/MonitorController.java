package com.example.pulseWatch.controller;

import com.example.pulseWatch.dto.MonitorDto;
import com.example.pulseWatch.entity.Monitor;
import com.example.pulseWatch.service.MonitorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/monitors")
public class MonitorController {
    private final MonitorService monitorService;

    public MonitorController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @GetMapping
    public List<Monitor> getAllMonitors(){
        return monitorService.getAllMonitors();
    }
    @GetMapping("/{id}")
    public Monitor getMonitorById(@PathVariable UUID id){
        return monitorService.getMonitorById(id);
    }
    @PostMapping
    public ResponseEntity<Monitor> createMonitor(
            @RequestBody @Valid MonitorDto monitorDto) {

        Monitor createdMonitor = monitorService.createMonitor(
                monitorDto.getName(),
                monitorDto.getUrl(),
                monitorDto.getCheckIntervalSeconds());


        return ResponseEntity.status(HttpStatus.CREATED).body(createdMonitor);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMonitor(@PathVariable UUID id) {
        monitorService.deleteMonitor(id);
        return ResponseEntity.noContent().build();
    }
}
