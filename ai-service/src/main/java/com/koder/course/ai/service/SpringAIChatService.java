package com.koder.course.ai.service;

import com.koder.course.ai.model.Answer;
import com.koder.course.ai.model.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Locale;

@Service("simpleAsk")
@Slf4j
public class SpringAIChatService implements AIChatService {

    private final ChatClient chatClient;

    public SpringAIChatService(ChatClient.Builder chatClientBuilder,
                               ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor())
                .build();

    }

    @Override
    public Mono<Answer> askQuestion(Question question, Locale locale) {

        return chatClient.prompt()
                .user(question.question())
                .stream()
                .content()
                .doOnNext(chunk -> log.info("Received AI chunk: {}", chunk))
                .reduce((accumulated, newContent) -> accumulated + newContent)
                .map(aws -> new Answer(question.question(), aws));
    }
}
