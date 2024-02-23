package com.example.pruebatecnica.controllers;

import com.example.pruebatecnica.entities.StatusEntity;
import com.example.pruebatecnica.entities.TaskEntity;
import com.example.pruebatecnica.services.StatusService;
import com.example.pruebatecnica.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/API/v1/task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    StatusService statusService;

    @PostMapping("/create")
    public ResponseEntity<TaskEntity> createTask(@RequestBody TaskEntity task) {
        // Verifica si el estado es nulo y establece un estado predeterminado si es necesario
        if (task.getStatus() == null) {
            task.setStatus(getDefaultStatus());
        }

        TaskEntity newTask = taskService.saveTask(task);
        return ResponseEntity.ok(newTask);
    }

    @GetMapping("/get")
    public ResponseEntity<Iterable<TaskEntity>> getAllTasks() {
        Iterable<TaskEntity> tasks = taskService.getAllTasks();

        if(tasks.iterator().hasNext()) {
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.notFound().build(); // Devolver ResponseEntity con código 404
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<TaskEntity> getTaskById(@PathVariable Integer id) {
        TaskEntity task = taskService.getTaskById(id);
        if (task != null) {
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.notFound().build(); // Devolver ResponseEntity con código 404
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TaskEntity> updateTask(@RequestBody TaskEntity task, @PathVariable Long id) {
        TaskEntity existingTask = taskService.getTaskById(Math.toIntExact(id));

        if (existingTask != null) {
            task.setId(id);
            TaskEntity updatedTask = taskService.updateTask(task);
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.notFound().build(); // Devolver ResponseEntity con código 404
        }
    }

    @PatchMapping("/update/status/{id}")
    public ResponseEntity<TaskEntity> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        TaskEntity updatedTask = taskService.updateTaskStatus(id, fields);
        return ResponseEntity.ok(updatedTask);
    }


    // Método para obtener el estado por defecto
    private StatusEntity getDefaultStatus() {
        // Lógica para obtener el estado por defecto (puedes definirlo directamente aquí o utilizar un servicio)
        StatusEntity defaultStatus = new StatusEntity();
        defaultStatus.setId(1); // Ajusta el ID según tu lógica
        defaultStatus.setName("Iniciada");
        defaultStatus.setDescription("Estado inicial de una tarea");
        return defaultStatus;
    }


}
