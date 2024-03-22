package com.example.todolist.controllers;

import com.example.todolist.dtos.TaskDTO;
import com.example.todolist.entities.Task;
import com.example.todolist.services.interfaces.ITaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final ITaskService taskService;

    @PostMapping
    public Mono<ResponseEntity<Task>> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        return taskService.createTask(taskDTO)
                .map(task -> ResponseEntity.status(HttpStatus.CREATED).body(task));
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Task>>> getAllTasks() {
        Flux<Task> tasks = taskService.getAllTasks();
        return tasks.collectList()
                .flatMap(list -> list.isEmpty()
                        ? Mono.just(ResponseEntity.noContent().build())
                        : Mono.just(ResponseEntity.ok(tasks)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Task>> getTaskById(@PathVariable UUID id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Task>> updateTask(@PathVariable UUID id, @Valid @RequestBody TaskDTO taskDTO) {
        return taskService.updateTask(id, taskDTO)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable UUID id) {
        return taskService.deleteTask(id)
                .then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT)));
    }
}
