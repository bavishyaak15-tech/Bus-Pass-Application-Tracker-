package com.buspass.bus_pass_tracker.controller;

import com.buspass.bus_pass_tracker.entity.PassModel;
import com.buspass.bus_pass_tracker.service.PassModelService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pass-models")
public class PassModelController {

    private final PassModelService service;

    public PassModelController(
            PassModelService service) {

        this.service = service;
    }


    @PostMapping
    public ResponseEntity<PassModel> create(
            @Valid @RequestBody PassModel model) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(model));
    }


    @GetMapping
    public List<PassModel> getAll() {

        return service.getAll();
    }


    @GetMapping("/{id}")
    public PassModel getById(
            @PathVariable Long id) {

        return service.getById(id);
    }


    @PutMapping("/{id}")
    public PassModel update(
            @PathVariable Long id,
            @Valid @RequestBody PassModel model) {

        return service.update(id, model);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}