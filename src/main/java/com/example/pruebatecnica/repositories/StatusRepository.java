package com.example.pruebatecnica.repositories;

import com.example.pruebatecnica.entities.StatusEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends CrudRepository<StatusEntity, Long> {
}
