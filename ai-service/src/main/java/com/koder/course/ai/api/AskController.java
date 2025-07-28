package com.koder.course.ai.api;

import com.koder.course.ai.model.Answer;
import com.koder.course.ai.model.Question;
import com.koder.course.ai.service.AIChatService;
import com.koder.course.ai.service.AiTaskSuggestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Locale;

@RestController
public class AskController {

    private final AIChatService chatService;
    private final AiTaskSuggestionService taskSuggestionService;

    public AskController(@Qualifier("simpleAsk") AIChatService chatService,
                         AiTaskSuggestionService taskSuggestionService) {
        this.chatService = chatService;
        this.taskSuggestionService = taskSuggestionService;
    }

    @PostMapping(value = "/ask", produces = "application/json")
    public  Mono<ResponseEntity<Answer>> askQuestion(@RequestBody @Valid Question question, ServerWebExchange exchange) {

        String acceptLanguage = exchange.getRequest().getHeaders().getFirst("Accept-Language");
        Locale locale = (acceptLanguage != null && !acceptLanguage.isBlank())
                ? Locale.forLanguageTag(acceptLanguage)
                : Locale.ENGLISH;

        return chatService.askQuestion(question, locale)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/suggest-task/{category}", produces = "application/json")
    public Mono<ResponseEntity<?>> suggestTasks(@PathVariable String category, ServerWebExchange exchange) {

        return taskSuggestionService.suggestTasks(category)
                .map(suggestions -> {
                    if (suggestions.isEmpty()) {
                        return ResponseEntity.noContent().build();
                    }
                    return ResponseEntity.ok(suggestions);
                })
                .onErrorReturn(ResponseEntity.badRequest().build());
    }
}
