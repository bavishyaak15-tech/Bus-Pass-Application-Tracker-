package com.buspass.bus_pass_tracker.repository;

import com.buspass.bus_pass_tracker.entity.PassModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassModelRepository
        extends JpaRepository<PassModel, Long> {

    boolean existsByModelName(String modelName);
}