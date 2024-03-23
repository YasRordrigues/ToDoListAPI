package com.example.todolist.services;

import com.example.todolist.dtos.TaskDTO;
import com.example.todolist.entities.Task;
import com.example.todolist.errors.TaskNotFoundException;
import com.example.todolist.errors.TaskTitleExistsException;
import com.example.todolist.errors.TaskValidationException;
import com.example.todolist.repositories.TaskRepository;
import com.example.todolist.services.interfaces.ITaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService implements ITaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    @Override
    public Mono<Task> createTask(TaskDTO taskDTO) {
        log.info("Attempting to create task: {}", taskDTO);
        LocalDateTime now = LocalDateTime.now();
        if (taskDTO.getExpirationDate().isBefore(now)) {
            log.error("Failed to create task, expiration date must be in the future.");
            return Mono.error(new TaskValidationException("Expiration date must be in the future."));
        }
        return this.taskRepository.findByTitle(taskDTO.getTitle())
                .flatMap(dbTask -> {
                    log.error("Failed to create task, a task with the given title '{}' already exists.", taskDTO.getTitle());
                    return Mono.error(new TaskTitleExistsException("A task with the given title already exists."));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    Task task = modelMapper.map(taskDTO, Task.class);
                    task.setCreationDate(now);
                    log.info("Task with title '{}' passed validation and will be saved.", task.getTitle());
                    return this.taskRepository.save(task);
                }))
                .cast(Task.class)
                .doOnSuccess(createdTask -> {
                    if (createdTask != null) {
                        log.info("Task '{}' created successfully.", createdTask.getTitle());
                    }
                })
                .doOnError(e -> log.error("Task creation failed due to an exception: {}", e.getMessage()));
    }



    @Override
    public Flux<Task> getAllTasks() {
        log.info("Fetching all tasks.");
        return taskRepository.findAll()
                .doOnComplete(() -> log.info("All tasks fetched successfully."))
                .doOnError(e -> log.error("Error fetching tasks: {}", e.getMessage()));
    }

    @Override
    public Mono<Task> getTaskById(UUID id) {
        log.info("Looking for task with id '{}'.", id);
        return taskRepository.findById(id)
                .doOnSuccess(task -> {
                    if (task != null) {
                        log.info("Task with id '{}' found: {}", id, task);
                    } else {
                        log.warn("Task with id '{}' not found.", id);
                    }
                })
                .switchIfEmpty(Mono.error(new TaskNotFoundException("Task not found with id: " + id)));
    }

    @Override
    public Mono<Task> updateTask(UUID id, TaskDTO taskDetails) {
        log.info("Attempting to update task with id '{}'.", id);
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Update failed, task with id '{}' not found.", id);
                    return Mono.error(new TaskNotFoundException("Task not found with id: " + id));
                }))
                .flatMap(existingTask -> {
                    if (taskDetails.getExpirationDate().isBefore(existingTask.getCreationDate())) {
                        log.error("Update failed, expiration date cannot be before creation date for task '{}'.", existingTask.getId());
                        return Mono.error(new TaskValidationException("Expiration date cannot be before creation date."));
                    }
                    log.info("Updating task '{}'.", existingTask.getId());
                    existingTask.setTitle(taskDetails.getTitle());
                    existingTask.setDescription(taskDetails.getDescription());
                    existingTask.setExpirationDate(taskDetails.getExpirationDate());
                    return taskRepository.save(existingTask);
                })
                .doOnSuccess(updatedTask -> log.info("Task with id '{}' updated successfully.", updatedTask.getId()))
                .doOnError(e -> log.error("Update failed for task with id '{}': {}", id, e.getMessage()));
    }

    @Override
    public Mono<Void> deleteTask(UUID id) {
        log.info("Attempting to delete task with id '{}'.", id);
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Delete failed, task with id '{}' not found.", id);
                    return Mono.error(new TaskNotFoundException("Task not found with id: " + id));
                }))
                .flatMap(task -> {
                    log.info("Task with id '{}' will be deleted.", task.getId());
                    return taskRepository.deleteById(id);
                })
                .doOnSuccess(aVoid -> log.info("Task with id '{}' deleted successfully.", id))
                .doOnError(e -> log.error("Delete failed for task with id '{}': {}", id, e.getMessage()));
    }
}
