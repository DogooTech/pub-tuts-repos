package com.koder.course.ai.service;

import com.koder.course.ai.model.TaskSuggestion;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AiTaskSuggestionService {

    Mono<List<TaskSuggestion>> suggestTasks(String category);
}
