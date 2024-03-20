package com.example.todolist.services;

import com.example.todolist.dtos.TaskDTO;
import com.example.todolist.entities.Task;
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
    public Mono<Task> createTask(TaskDTO createTaskDTO) {
        Task task = modelMapper.map(createTaskDTO, Task.class);
        task.setCreationDate(LocalDateTime.now()); // Define a data de criação no momento da criação
        return taskRepository.save(task);
    }

    @Override
    public Flux<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Mono<Task> getTaskById(UUID id) {
        return taskRepository.findById(id);
    }

    @Override
    public Mono<Task> updateTask(UUID id, TaskDTO taskDetails) {
        return taskRepository.findById(id)
                .flatMap(task -> {
                    task.setTitle(taskDetails.getTitle());
                    task.setDescription(taskDetails.getDescription());
                    task.setExpirationDate(taskDetails.getExpirationDate());
                    return taskRepository.save(task);
                });
    }

    @Override
    public Mono<Void> deleteTask(UUID id) {
        return taskRepository.deleteById(id);
    }
}
