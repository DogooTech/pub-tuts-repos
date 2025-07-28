package com.koder.course.ai.model;

import java.time.LocalDateTime;

public record TaskSuggestion(String title,
                             String description,
                             LocalDateTime startDate,
                             LocalDateTime endDate,
                             int priority,
                             String assignedTo,
                             String approvalUser) {

}
