package com.example.todolist.services;

import com.example.todolist.dtos.TaskDTO;
import com.example.todolist.entities.Task;
import com.example.todolist.errors.TaskNotFoundException;
import com.example.todolist.errors.TaskTitleExistsException;
import com.example.todolist.errors.TaskValidationException;
import com.example.todolist.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDTO taskDTO;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        task = new Task();
        task.setId(id);
        task.setTitle("Task Title");
        task.setDescription("Task Description");
        task.setCreationDate(LocalDateTime.now());
        task.setExpirationDate(LocalDateTime.now().plusDays(1));

        taskDTO = new TaskDTO();
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setExpirationDate(task.getExpirationDate());
    }

    @Test
    void whenCreateTaskWithValidData_thenTaskIsCreated() {
        when(modelMapper.map(any(TaskDTO.class), eq(Task.class))).thenReturn(task);
        when(taskRepository.findByTitle(taskDTO.getTitle())).thenReturn(Mono.empty());
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(task));

        StepVerifier.create(taskService.createTask(taskDTO))
                .expectNextMatches(createdTask -> createdTask.getTitle().equals(taskDTO.getTitle()))
                .verifyComplete();

        verify(taskRepository).findByTitle(taskDTO.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void whenCreateTaskWithPastExpirationDate_thenThrowTaskValidationException() {
        taskDTO.setExpirationDate(LocalDateTime.now().minusDays(1));

        StepVerifier.create(taskService.createTask(taskDTO))
                .expectError(TaskValidationException.class)
                .verify();
    }

    @Test
    void whenCreateTaskWithTitleExists_thenThrowTaskTitleExistsException() {
        when(taskRepository.findByTitle(taskDTO.getTitle())).thenReturn(Mono.just(task));

        StepVerifier.create(taskService.createTask(taskDTO))
                .expectError(TaskTitleExistsException.class)
                .verify();
    }

    @Test
    void whenGetAllTasks_thenReturnFluxOfTasks() {
        when(taskRepository.findAll()).thenReturn(Flux.just(task, task));

        StepVerifier.create(taskService.getAllTasks())
                .expectNext(task, task)
                .verifyComplete();
    }

    @Test
    void whenGetTaskById_thenReturnTask() {
        when(taskRepository.findById(id)).thenReturn(Mono.just(task));

        StepVerifier.create(taskService.getTaskById(id))
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    void whenGetTaskById_thenNotFound() {
        when(taskRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(taskService.getTaskById(id))
                .expectError(TaskNotFoundException.class)
                .verify();
    }

    @Test
    void whenUpdateTaskWithValidData_thenTaskIsUpdated() {
        when(taskRepository.findById(id)).thenReturn(Mono.just(task));
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(task));

        taskDTO.setExpirationDate(LocalDateTime.now().plusDays(2)); // Ensure the expiration date is valid

        StepVerifier.create(taskService.updateTask(id, taskDTO))
                .expectNextMatches(updatedTask -> updatedTask.getTitle().equals(taskDTO.getTitle()) && updatedTask.getDescription().equals(taskDTO.getDescription()))
                .verifyComplete();

        verify(taskRepository).findById(id);
        verify(taskRepository).save(task);
    }

    @Test
    void whenUpdateTaskWithInvalidExpirationDate_thenThrowTaskValidationException() {
        Task existingTask = new Task();
        existingTask.setId(id);
        existingTask.setCreationDate(LocalDateTime.now());
        existingTask.setExpirationDate(LocalDateTime.now().plusDays(1));

        when(taskRepository.findById(id)).thenReturn(Mono.just(existingTask));
        taskDTO.setExpirationDate(LocalDateTime.now().minusDays(1)); // Expiration date before
        // Set expiration date before creation date for validation check
        taskDTO.setExpirationDate(LocalDateTime.now().minusDays(1));

        StepVerifier.create(taskService.updateTask(id, taskDTO))
                .expectError(TaskValidationException.class)
                .verify();

        verify(taskRepository).findById(id);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void whenUpdateTaskWithNonExistentId_thenThrowTaskNotFoundException() {
        when(taskRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(taskService.updateTask(id, taskDTO))
                .expectError(TaskNotFoundException.class)
                .verify();

        verify(taskRepository).findById(id);
    }

    @Test
    void whenDeleteTaskWithValidId_thenTaskIsDeleted() {
        when(taskRepository.findById(id)).thenReturn(Mono.just(task));
        when(taskRepository.deleteById(id)).thenReturn(Mono.empty());

        StepVerifier.create(taskService.deleteTask(id))
                .verifyComplete();

        verify(taskRepository).findById(id);
        verify(taskRepository).deleteById(id);
    }

    @Test
    void whenDeleteTaskWithInvalidId_thenThrowTaskNotFoundException() {
        when(taskRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(taskService.deleteTask(id))
                .expectError(TaskNotFoundException.class)
                .verify();

        verify(taskRepository).findById(id);
        verify(taskRepository, never()).deleteById(id);
    }
}
