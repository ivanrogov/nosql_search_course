package com.epam.example.model;

import java.time.LocalDate;
import java.util.List;

public class Task {

    public Task() {
    }

    public Task(String name, String description, LocalDate createdDate, LocalDate deadline, String category, List<Subtask> subtasks) {
        this.name = name;
        this.description = description;
        this.createdDate = createdDate;
        this.deadline = deadline;
        this.category = category;
        this.subtasks = subtasks;
    }

    private String id;
    private LocalDate createdDate;
    private LocalDate deadline;
    private String name;
    private String description;
    private List<Subtask> subtasks;
    private String category;

    public Task(String id, LocalDate parse, LocalDate parse1, String name, String description, String category) {
        this.id = id;
        this.createdDate = parse;
        this.deadline = parse1;
        this.name = name;
        this.description = description;
        this.category = category;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
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

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", createdDate=" + createdDate +
                ", deadline=" + deadline +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", subtasks=" + subtasks +
                ", category='" + category + '\'' +
                '}';
    }
}
