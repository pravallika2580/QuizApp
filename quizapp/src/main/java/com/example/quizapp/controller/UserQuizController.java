package com.example.quizapp.controller;

import com.example.quizapp.dto.*;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.service.QuestionService;
import com.example.quizapp.service.QuizAttemptService;
import com.example.quizapp.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserQuizController {

    private final QuizService quizService;
    private final QuestionService questionService;
    private final QuizAttemptService quizAttemptService;

    // GET /api/user/quizzes
    @GetMapping("/quizzes")
    public ResponseEntity<List<QuizResponse>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    // GET /api/user/quizzes/{quizId}
    @GetMapping("/quizzes/{quizId}")
    public ResponseEntity<QuizWithQuestionsResponse> getQuizWithQuestions(@PathVariable Long quizId) {
        Quiz quiz = quizService.findQuizOrThrow(quizId);
        List<QuestionUserResponse> questions = questionService.getQuestionsForUser(quizId);

        QuizWithQuestionsResponse response = QuizWithQuestionsResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .category(quiz.getCategory())
                .duration(quiz.getDuration())
                .totalMarks(quiz.getTotalMarks())
                .questions(questions)
                .build();

        return ResponseEntity.ok(response);
    }

    // POST /api/user/quizzes/{quizId}/submit
    @PostMapping("/quizzes/{quizId}/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(@PathVariable Long quizId,
                                                           @Valid @RequestBody SubmitQuizRequest request) {
        return ResponseEntity.ok(quizAttemptService.submitQuiz(quizId, request));
    }

    // GET /api/user/quizzes/{quizId}/result
    @GetMapping("/quizzes/{quizId}/result")
    public ResponseEntity<QuizResultResponse> getResult(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizAttemptService.getLatestResult(quizId));
    }
}
