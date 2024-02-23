package com.example.pruebatecnica.services;

import com.example.pruebatecnica.entities.StatusEntity;
import com.example.pruebatecnica.repositories.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusService {
    @Autowired
    StatusRepository statusRepository;

    // Development
    public StatusEntity saveStatus(StatusEntity status) {
        return statusRepository.save(status);
    }

    // Development
    public Iterable<StatusEntity> getAllStatus() {
        return statusRepository.findAll();
    }

    public StatusEntity getStatusById(Integer id) {
        return statusRepository.findById(Long.valueOf(id)).get();
    }
}
