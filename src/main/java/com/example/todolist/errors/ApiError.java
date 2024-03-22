package com.example.todolist.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private String error;
    private String message;
    private List<String> details;

}
