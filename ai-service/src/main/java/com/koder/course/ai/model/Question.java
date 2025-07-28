package com.koder.course.ai.model;

import jakarta.validation.constraints.NotBlank;

public record Question(String taskTitle,
                       String taskDescription,
                       @NotBlank(message = "Question is required") String question) {
}
