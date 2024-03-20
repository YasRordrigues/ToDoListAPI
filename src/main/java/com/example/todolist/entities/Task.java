package com.example.todolist.entities;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ToString
@Table("tasks")
public class Task {

    @Id
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;

}
