package com.buspass.bus_pass_tracker.service;

import com.buspass.bus_pass_tracker.entity.PassModel;
import com.buspass.bus_pass_tracker.exception.BusinessRuleException;
import com.buspass.bus_pass_tracker.exception.ResourceNotFoundException;
import com.buspass.bus_pass_tracker.repository.PassModelRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassModelService {

    private final PassModelRepository repository;

    public PassModelService(
            PassModelRepository repository) {

        this.repository = repository;
    }

    public PassModel create(PassModel model) {

        if (repository.existsByModelName(
                model.getModelName())) {

            throw new BusinessRuleException(
                    "Pass model name already exists"
            );
        }

        return repository.save(model);
    }

    public List<PassModel> getAll() {
        return repository.findAll();
    }

    public PassModel getById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pass model not found with id: " + id
                        )
                );
    }

    public PassModel update(
            Long id,
            PassModel input) {

        PassModel existing = getById(id);

        if (!existing.getModelName()
                .equals(input.getModelName())
                && repository.existsByModelName(
                        input.getModelName())) {

            throw new BusinessRuleException(
                    "Pass model name already exists"
            );
        }

        existing.setModelName(
                input.getModelName()
        );

        existing.setDescription(
                input.getDescription()
        );

        existing.setFee(
                input.getFee()
        );

        return repository.save(existing);
    }

    public void delete(Long id) {

        PassModel model = getById(id);

        repository.delete(model);
    }
}