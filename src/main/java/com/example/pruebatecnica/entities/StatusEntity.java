package com.example.pruebatecnica.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;

import java.util.List;

@Entity
@Table(name = "status")
public class StatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String description;

    @OneToMany(fetch =  FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<TaskEntity> task;

    public StatusEntity() {
    }

    public StatusEntity(Long id, String name, String description, TaskEntity task) {
        this.name = name;
        this.description = description;
        this.id = Math.toIntExact(id);
    }

    public StatusEntity(Long newStatusId) {
    }

    public StatusEntity(Integer id, String name, String description) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TaskEntity> getTask() {
        return task;
    }

    public void setTask(List<TaskEntity> task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return "StatusEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", task=" + task +
                '}';
    }
}
