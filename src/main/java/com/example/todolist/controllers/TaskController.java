package com.example.todolist.controllers;

import com.example.todolist.dtos.TaskDTO;
import com.example.todolist.entities.Task;
import com.example.todolist.services.interfaces.ITaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Task api", description = "endpoints for tasks")
public class TaskController {

    private final ITaskService taskService;

    @PostMapping
    @Operation(summary = "Create a new task", description = "Creates a new task and returns the created task details")
    @ApiResponse(responseCode = "201", description = "Task successfully created")
    @ApiResponse(responseCode = "400", description = "Bad request if the request data is invalid")
    @ApiResponse(responseCode = "409", description = "Conflict if a task with the same details already exists")
    @ApiResponse(responseCode = "422", description = "Unprocessable Entity if the entity is incorrect")
    public Mono<ResponseEntity<Task>> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        log.info("Creating a new task: {}", taskDTO);
        return taskService.createTask(taskDTO)
                .map(task -> {
                    log.info("Task created: {}", task);
                    return ResponseEntity.status(HttpStatus.CREATED).body(task);
                });
    }

    @GetMapping
    @ApiResponse(responseCode = "200", description = "Successful retrieval of task list", content = @Content)
    @ApiResponse(responseCode = "204", description = "No tasks available")
    public Mono<ResponseEntity<Flux<Task>>> getAllTasks() {
        log.info("Requested to get all tasks");
        Flux<Task> tasks = taskService.getAllTasks();
        return tasks.collectList().map(list ->
                list.isEmpty() ?
                        new ResponseEntity<>(null, HttpStatus.NO_CONTENT) :
                        new ResponseEntity<>(tasks, HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find task by ID", description = "Returns the task details for a given ID")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of task details", content = @Content)
    @ApiResponse(responseCode = "404", description = "Task not found")
    public Mono<ResponseEntity<Task>> getTaskById(@PathVariable UUID id) {
        log.info("Requested to get task by ID: {}", id);
        return taskService.getTaskById(id)
                .map(task -> {
                    log.info("Task found: {}", task);
                    return ResponseEntity.ok(task);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task", description = "Updates a task and returns the updated task details")
    @ApiResponse(responseCode = "200", description = "Task successfully updated")
    @ApiResponse(responseCode = "400", description = "Bad request if the request data is invalid")
    @ApiResponse(responseCode = "404", description = "Not Found if the task with the specified ID does not exist")
    @ApiResponse(responseCode = "409", description = "Conflict if a task with the same details already exists")
    @ApiResponse(responseCode = "422", description = "Unprocessable Entity if the entity is incorrect")
    public Mono<ResponseEntity<Task>> updateTask(@PathVariable UUID id, @Valid @RequestBody TaskDTO taskDTO) {
        log.info("Updating task with ID: {} with data: {}", id, taskDTO);
        return taskService.updateTask(id, taskDTO)
                .map(updatedTask -> {
                    log.info("Task updated: {}", updatedTask);
                    return ResponseEntity.ok(updatedTask);
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", description = "Deletes a task based on the given ID")
    @ApiResponse(responseCode = "204", description = "Task successfully deleted")
    @ApiResponse(responseCode = "400", description = "Bad request if the deletion cannot be performed")
    @ApiResponse(responseCode = "404", description = "Not Found if the task with the specified ID does not exist")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable UUID id) {
        log.info("Deleting task with ID: {}", id);
        return taskService.deleteTask(id)
                .then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT)));
    }
}
