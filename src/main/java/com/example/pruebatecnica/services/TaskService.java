package com.example.pruebatecnica.services;

import com.example.pruebatecnica.entities.StatusEntity;
import com.example.pruebatecnica.entities.TaskEntity;
import com.example.pruebatecnica.repositories.StatusRepository;
import com.example.pruebatecnica.repositories.TaskRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    EntityManager entityManager;

    public Iterable<TaskEntity> getAllTasks() {
        return taskRepository.findAll();
    }

    public TaskEntity getTaskById(Integer id) {
        return taskRepository.findById(Long.valueOf(id)).get();
    }

    @Transactional
    public TaskEntity saveTask(TaskEntity task) {

        // Verifica si la entidad está detached
        if (entityManager.contains(task)) {
            // La entidad ya está managed, no es necesario hacer merge
            return taskRepository.save(task);
        } else {
            // La entidad está detached, realiza merge para convertirla a managed
            return taskRepository.save(entityManager.merge(task));
        }
    }

    public TaskEntity updateTaskStatus(Long id, Map<String, Object> taskStatusMap) {

        // Verifica si el Map tiene una entrada llamada "status"
        if (taskStatusMap.containsKey("status")) {
            Map<String, Object> statusMap = (Map<String, Object>) taskStatusMap.get("status");

            // Extrae el id del status y conviértelo a Long
            Long newStatusId = Long.valueOf(statusMap.get("id").toString());

            // Resto del código permanece igual
            TaskEntity taskEntity = taskRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));

            StatusEntity newStatus = statusRepository.findById(newStatusId)
                    .orElseThrow(() -> new EntityNotFoundException("Status with id " + newStatusId + " not found"));

            taskEntity.setStatus(newStatus);
            taskRepository.save(taskEntity);

            return taskEntity;
        } else {
            // Manejar caso en el que no hay entrada "status" en el Map
            throw new IllegalArgumentException("No 'status' field found in the request.");
        }
    }

    private StatusEntity convertMapToStatusEntity(Map<String, Object> statusMap) {
        Integer id = (Integer) statusMap.get("id");
        String name = (String) statusMap.get("name");
        String description = (String) statusMap.get("description");

        return new StatusEntity(id, name, description);
    }

    public TaskEntity updateTask(TaskEntity task) {
        return taskRepository.save(task);
    }

    public String deleteTask(Integer id) {
        taskRepository.deleteById(Long.valueOf(id));
        return "Task deleted";
    }

}
