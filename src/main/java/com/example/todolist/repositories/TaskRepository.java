package com.example.todolist.repositories;


import com.example.todolist.entities.Task;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface TaskRepository extends ReactiveCrudRepository<Task, UUID> {
   Mono<Task> findByTitle(String title);
}