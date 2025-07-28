package com.koder.course.ai.service;

import com.koder.course.ai.contants.TaskConstants;
import com.koder.course.ai.exception.AnswerNotRelevantException;
import com.koder.course.ai.model.Answer;
import com.koder.course.ai.model.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service("selfEvaluating")
public class SelfEvaluatingSpringAIChatService implements AIChatService {

    @Value("classpath:/promptTemplates/taskQuestionPromptTemplate.st")
    Resource questionPromptTemplate;

    @Value("classpath:/promptTemplates/taskQuestionPromptTemplate_lang_vi.st")
    Resource questionPromptTemplateVi;

    private final ChatClient chatClient;
    private final RelevancyEvaluator evaluator;

    public SelfEvaluatingSpringAIChatService(ChatClient.Builder chatClientBuilder,
                                             ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor())
                .build();
        this.evaluator = new RelevancyEvaluator(chatClientBuilder); // #1
    }

    @Override
    public Mono<Answer> askQuestion(Question question, Locale locale) {
        return chatClient.prompt()
                .user(usrSpec -> usrSpec
                        .text(locale.getLanguage().equals("vi") ? questionPromptTemplateVi : questionPromptTemplate)
                        .param(TaskConstants.TASK_TITLE, question.taskTitle())
                        .param(TaskConstants.TASK_DESCRIPTION, question.taskDescription())
                        .param(TaskConstants.QUESTION, question.question()))
                .stream()
                .content()
                .doOnNext(chunk -> log.info("Received AI chunk: {}", chunk))
                .reduce((accumulated, newContent) -> accumulated + newContent)
                .doOnNext(answer -> evaluateRelevancy(question, answer))
                .map(asw -> new Answer(question.taskTitle(), asw)) // #2
                .timeout(Duration.ofSeconds(60))
                .onErrorResume(TimeoutException.class, ex ->
                        Mono.just(new Answer(question.taskTitle(), "Sorry, the request timed out."))
                ).retryWhen(Retry
                        .max(3)
                        .filter(throwable -> throwable instanceof AnswerNotRelevantException)
                ).onErrorResume(AnswerNotRelevantException.class, ex ->
                        Mono.just(new Answer(question.taskTitle(), "Sorry, I couldn't find a relevant answer."))
                );
    }

    private void evaluateRelevancy(Question question, String answer) {
        EvaluationRequest evaluationRequest =
                new EvaluationRequest(question.question(), List.of(), answer);

        EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);

        if (!evaluationResponse.isPass()) {
            throw new AnswerNotRelevantException(question.question() + answer); // #4
        }
    }
}
