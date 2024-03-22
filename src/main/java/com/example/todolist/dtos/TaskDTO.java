package com.example.todolist.dtos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDTO {
    @NotBlank(message = "Title is required.")
    private String title;

    private String description;

    @NotNull(message = "Expiration date is required.")
    private LocalDateTime expirationDate;
}