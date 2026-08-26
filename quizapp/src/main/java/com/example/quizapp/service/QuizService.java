package com.example.quizapp.service;

import com.example.quizapp.dto.QuizRequest;
import com.example.quizapp.dto.QuizResponse;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;

    @Transactional
    public QuizResponse createQuiz(QuizRequest request) {
        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .duration(request.getDuration())
                .totalMarks(request.getTotalMarks())
                .build();

        Quiz saved = quizRepository.save(quiz);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<QuizResponse> getAllQuizzes() {
        return quizRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuizResponse getQuiz(Long quizId) {
        Quiz quiz = findQuizOrThrow(quizId);
        return toResponse(quiz);
    }

    @Transactional
    public QuizResponse updateQuiz(Long quizId, QuizRequest request) {
        Quiz quiz = findQuizOrThrow(quizId);

        quiz.setTitle(request.getTitle());
        quiz.setCategory(request.getCategory());
        quiz.setDuration(request.getDuration());
        quiz.setTotalMarks(request.getTotalMarks());

        Quiz saved = quizRepository.save(quiz);
        return toResponse(saved);
    }

    @Transactional
    public void deleteQuiz(Long quizId) {
        Quiz quiz = findQuizOrThrow(quizId);
        // Associated questions are removed automatically via cascade + orphanRemoval
        quizRepository.delete(quiz);
    }

    @Transactional(readOnly = true)
    public Quiz findQuizOrThrow(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
    }

    private QuizResponse toResponse(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .category(quiz.getCategory())
                .duration(quiz.getDuration())
                .totalMarks(quiz.getTotalMarks())
                .build();
    }
}
