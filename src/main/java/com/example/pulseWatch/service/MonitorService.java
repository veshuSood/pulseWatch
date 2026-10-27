package com.example.pulseWatch.service;

import com.example.pulseWatch.entity.Monitor;
import com.example.pulseWatch.repository.MonitorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.pulseWatch.entity.User;
import com.example.pulseWatch.repository.UserRepository;
import com.example.pulseWatch.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

@Service
public class MonitorService {
    private final MonitorRepository monitorRepository;
    private final UserRepository userRepository;
    private final UrlValidationService urlValidationService;

    public MonitorService(
            MonitorRepository monitorRepository,
            UserRepository userRepository,
            UrlValidationService urlValidationService
    ) {
        this.monitorRepository = monitorRepository;
        this.userRepository = userRepository;
        this.urlValidationService = urlValidationService;
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof UserDetailsImpl userDetails)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        return userDetails.getUser();
    }
    public List<Monitor> getAllMonitors() {
        User currentUser = getCurrentUser();

        return monitorRepository.findAllByUser_Id(currentUser.getId());
    }
    public Monitor getMonitorById(UUID id) {
        User currentUser = getCurrentUser();

        return monitorRepository.findByIdAndUser_Id(
                        id, currentUser.getId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Monitor not found"
                        ));
    }
    public Monitor createMonitor(
            String name,
            String url,
            int checkIntervalSeconds
    ) {
        User currentUser = getCurrentUser();

        urlValidationService.validate(url);

        Monitor newMonitor = new Monitor(name, url.trim());
        newMonitor.setCheck_interval_seconds(checkIntervalSeconds);
        newMonitor.setUser(currentUser);

        return monitorRepository.save(newMonitor);
    }
    public void deleteMonitor(UUID id) {
        Monitor monitor = getMonitorById(id);
        monitorRepository.delete(monitor);
    }
}
