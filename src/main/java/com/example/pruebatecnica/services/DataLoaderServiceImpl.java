package com.example.pruebatecnica.services;

import com.example.pruebatecnica.entities.PriorityEntity;
import com.example.pruebatecnica.entities.StatusEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataLoaderServiceImpl implements DataLoaderService{
    @Autowired
    private StatusService statusService;

    @Autowired
    private PriorityService priorityService;

    @Override
    public void loadInitialData(){

        // Crear y guardar estados
        StatusEntity status1 = new StatusEntity();
        StatusEntity status2 = new StatusEntity();
        StatusEntity status3 = new StatusEntity();

        // Cargar estado 1 (Iniciada)
        status1.setName("Iniciada");
        status1.setDescription("Estado de tarea iniciado");
        statusService.saveStatus(status1);

        // Cargar estado 2 (En Proceso)
        status2.setName("En Progreso");
        status2.setDescription("Tarea en progreso");
        statusService.saveStatus(status2);

        // Cargar estado 3 (Finalizada)
        status3.setName("Finalizada");
        status3.setDescription("Tarea Finalizada");
        statusService.saveStatus(status3);

        // Crear y guardar prioridades
        PriorityEntity priority1 = new PriorityEntity();
        PriorityEntity priority2 = new PriorityEntity();
        PriorityEntity priority3 = new PriorityEntity();

        // Cargar prioridad 1 (Bajo)
        priority1.setName("Bajo");
        priority1.setDescription("Prioridad baja");
        priorityService.savePriority(priority1);

        // Cargar prioridad 2 (Normal)
        priority2.setName("Normal");
        priority2.setDescription("Prioridad normal");
        priorityService.savePriority(priority2);

        // Cargar prioridad 3 (Urgente)
        priority3.setName("Urgente");
        priority3.setDescription("Prioridad urgente");
        priorityService.savePriority(priority3);

    }
}
