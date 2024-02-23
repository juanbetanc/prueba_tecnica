package com.example.pruebatecnica.repositories;

import com.example.pruebatecnica.entities.PriorityEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriorityRepository extends CrudRepository<PriorityEntity, Long>{
}
