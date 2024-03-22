package com.example.todolist.services;

import com.example.todolist.dtos.TaskDTO;
import com.example.todolist.entities.Task;
import com.example.todolist.errors.TaskNotFoundException;
import com.example.todolist.errors.TaskTitleExistsException;
import com.example.todolist.errors.TaskValidationException;
import com.example.todolist.repositories.TaskRepository;
import com.example.todolist.services.interfaces.ITaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    @Override
    public Mono<Task> createTask(TaskDTO taskDTO) {
        LocalDateTime now = LocalDateTime.now();
        if (taskDTO.getExpirationDate().isBefore(now)) {
            return Mono.error(new TaskValidationException("Expiration date must be in the future."));
        }
        return this.taskRepository.findByTitle(taskDTO.getTitle())
                .flatMap(dbTask -> Mono.<Task>error(new TaskTitleExistsException("A task with the given title already exists.")))
                .switchIfEmpty(Mono.defer(() -> {
                    Task task = modelMapper.map(taskDTO, Task.class);
                    task.setCreationDate(now);
                    return this.taskRepository.save(task);
                }));
    }

    @Override
    public Flux<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Mono<Task> getTaskById(UUID id) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new TaskNotFoundException("Task not found with id: " + id)));
    }

    @Override
    public Mono<Task> updateTask(UUID id, TaskDTO taskDetails) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new TaskNotFoundException("Task not found with id: " + id)))

                .flatMap(existingTask -> {
                    if (taskDetails.getExpirationDate().isBefore(existingTask.getCreationDate())) {
                        return Mono.error(new TaskValidationException("Expiration date cannot be before creation date."));
                    }
                    existingTask.setTitle(taskDetails.getTitle());
                    existingTask.setDescription(taskDetails.getDescription());
                    existingTask.setExpirationDate(taskDetails.getExpirationDate());
                    return taskRepository.save(existingTask);
                });
    }


    @Override
    public Mono<Void> deleteTask(UUID id) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new TaskNotFoundException("Task not found with id: " + id)))
                .flatMap(task -> taskRepository.deleteById(id));
    }


}
