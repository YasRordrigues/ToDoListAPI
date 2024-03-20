package com.example.todolist.repositories;


import com.example.todolist.entities.Task;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface TaskRepository extends ReactiveCrudRepository<Task, UUID> {

}