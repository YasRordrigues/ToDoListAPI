package com.example.todolist.controllers;

import com.example.todolist.dtos.TaskDTO;
import com.example.todolist.entities.Task;
import com.example.todolist.services.interfaces.ITaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = TaskController.class)
@Import(ITaskService.class)
public class TaskControllerTest {

    @MockBean
    private ITaskService taskService;

    @Autowired
    private WebTestClient webTestClient;

    private Task task;
    private TaskDTO taskDTO;
    private UUID id;

    @BeforeEach
    public void setUp() {
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
    void whenCreateTask_thenStatusCreated() {
        when(taskService.createTask(any(TaskDTO.class))).thenReturn(Mono.just(task));

        webTestClient.post().uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(taskDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Task.class).isEqualTo(task);
    }

    @Test
    void whenGetAllTasks_thenStatusOk() {
        when(taskService.getAllTasks()).thenReturn(Flux.just(task, task));

        webTestClient.get().uri("/tasks")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Task.class).hasSize(2);
    }

    @Test
    void whenGetAllTasks_thenStatusNoContent() {
        when(taskService.getAllTasks()).thenReturn(Flux.empty());

        webTestClient.get().uri("/tasks")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void whenGetTaskById_thenStatusOk() {
        when(taskService.getTaskById(id)).thenReturn(Mono.just(task));

        webTestClient.get().uri("/tasks/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class).isEqualTo(task);
    }

    @Test
    void whenGetTaskById_thenStatusNotFound() {
        when(taskService.getTaskById(id)).thenReturn(Mono.empty());

        webTestClient.get().uri("/tasks/{id}", id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUpdateTask_thenStatusOk() {
        when(taskService.updateTask(any(UUID.class), any(TaskDTO.class))).thenReturn(Mono.just(task));

        webTestClient.put().uri("/tasks/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(taskDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class).isEqualTo(task);
    }

    @Test
    void whenDeleteTask_thenStatusNoContent() {
        when(taskService.deleteTask(id)).thenReturn(Mono.empty());

        webTestClient.delete().uri("/tasks/{id}", id)
                .exchange()
                .expectStatus().isNoContent();
    }
}
