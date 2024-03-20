package com.example.todolist.services.interfaces;

import com.example.todolist.dtos.TaskDTO;
import com.example.todolist.entities.Task;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ITaskService {

    Mono<Task> createTask(TaskDTO createTaskDTO);

    Flux<Task> getAllTasks();

    Mono<Task> getTaskById(UUID id);

    Mono<Task> updateTask(UUID id, TaskDTO taskDetails);

    Mono<Void> deleteTask(UUID id);
}
