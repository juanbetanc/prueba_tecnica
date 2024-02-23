package com.example.pruebatecnica.services;

import com.example.pruebatecnica.entities.PriorityEntity;
import com.example.pruebatecnica.repositories.PriorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PriorityService {
    @Autowired
    PriorityRepository priorityRepository;

    // Development
    public PriorityEntity savePriority(PriorityEntity priority) {
        return priorityRepository.save(priority);
    }

    // Development
    public Iterable<PriorityEntity> getAllPriorities() {
        return priorityRepository.findAll();
    }

    public PriorityEntity getPriorityById(Integer id) {
        return priorityRepository.findById(Long.valueOf(id)).get();
    }
}
