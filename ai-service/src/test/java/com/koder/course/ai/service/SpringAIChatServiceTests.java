package com.koder.course.ai.service;

import com.koder.course.ai.model.Answer;
import com.koder.course.ai.model.Question;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.Objects;

@SpringBootTest
public class SpringAIChatServiceTests {

    @Autowired
    private SpringAIChatService springAIChatService;

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    private RelevancyEvaluator relevancyEvaluator;

    private FactCheckingEvaluator factCheckingEvaluator;

    @BeforeEach
    public void setup() {
        this.relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
        this.factCheckingEvaluator = new FactCheckingEvaluator(
                chatClientBuilder);

    }

//    @Test
//    void evaluateRelevancy() {
//        String userText = "why is the sky blue?"; // #1
//
//        Question question = new Question(userText);
//
//        Answer answer = springAIChatService.askQuestion(question).block(); // #3
//
//        EvaluationRequest evaluationRequest = new EvaluationRequest(
//                userText, answer.answer());
//
//        String referenceAnswer =
//                "The sky is blue because of that was the paint color that was on sale.";
//
//        EvaluationResponse response =
//                factCheckingEvaluator.evaluate(evaluationRequest);
////
////        EvaluationResponse response = relevancyEvaluator
////                .evaluate(evaluationRequest); // #4
//
//        Assertions.assertThat(response.isPass()) // #5
//                .withFailMessage("""
//                    ========================================
//                    The answer "%s"
//                    is not considered relevant to the question
//                    "%s".
//                    ========================================
//                    """, answer.answer(), userText)
//                .isTrue();
//
//    }
}
