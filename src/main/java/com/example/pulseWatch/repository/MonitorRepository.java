package com.example.pulseWatch.repository;

import com.example.pulseWatch.entity.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MonitorRepository extends JpaRepository<Monitor, UUID> {
    List<Monitor> findAllByUser_Id(UUID userId);
    Optional<Monitor> findByIdAndUser_Id(UUID id, UUID userId);
}
