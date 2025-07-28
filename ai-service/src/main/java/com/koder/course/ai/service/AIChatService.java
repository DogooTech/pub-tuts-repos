package com.koder.course.ai.service;

import com.koder.course.ai.model.Answer;
import com.koder.course.ai.model.Question;
import reactor.core.publisher.Mono;

import java.util.Locale;

public interface AIChatService {
    Mono<Answer> askQuestion(Question question, Locale locale);
}
