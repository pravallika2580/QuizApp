package com.example.quizapp.service;

import com.example.quizapp.dto.QuizResultResponse;
import com.example.quizapp.dto.SubmitQuizRequest;
import com.example.quizapp.dto.UserAnswerRequest;
import com.example.quizapp.entity.*;
import com.example.quizapp.exception.BadRequestException;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.repository.QuizAttemptRepository;
import com.example.quizapp.repository.UserAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final QuizService quizService;

    @Transactional
        public QuizResultResponse submitQuiz(Long quizId, SubmitQuizRequest request) {
        Quiz quiz = quizService.findQuizOrThrow(quizId);
        List<Question> quizQuestions = questionRepository.findByQuiz_Id(quizId);
        Map<Long, Question> questionById = quizQuestions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        QuizAttempt attempt = QuizAttempt.builder()
            .quiz(quiz)
            .totalMarks(quiz.getTotalMarks())
            .score(0)
            .percentage(0.0)
            .status(AttemptStatus.STARTED)
            .startedAt(LocalDateTime.now())
            .build();
        attempt = quizAttemptRepository.save(attempt);

        int totalScore = 0;

        for (UserAnswerRequest answerRequest : request.getAnswers()) {
            Question question = questionById.get(answerRequest.getQuestionId());

            // Rule 2 - question must belong to the quiz tied to this attempt
            if (question == null) {
                throw new BadRequestException(
                        "Question " + answerRequest.getQuestionId() + " does not belong to this quiz");
            }

            // Rule 3 - selectedAnswer is already restricted to A/B/C/D by validation

            boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(answerRequest.getSelectedAnswer());
            int marksObtained = isCorrect ? question.getMarks() : 0;
            totalScore += marksObtained;

            UserAnswer userAnswer = UserAnswer.builder()
                    .attempt(attempt)
                    .questionId(question.getId())
                    .selectedAnswer(answerRequest.getSelectedAnswer())
                    .marksObtained(marksObtained)
                    .build();

            userAnswerRepository.save(userAnswer);
        }

        // Rule 4 - questions not included in the submission simply contribute 0 marks
        // (already the case, since only submitted answers add to totalScore)

        int totalMarks = attempt.getTotalMarks();
        double percentage = totalMarks > 0 ? (totalScore * 100.0) / totalMarks : 0.0;

        attempt.setScore(totalScore);
        attempt.setPercentage(percentage);
        attempt.setStatus(AttemptStatus.SUBMITTED);
        attempt.setSubmittedAt(LocalDateTime.now());

        QuizAttempt saved = quizAttemptRepository.save(attempt);

        return toResultResponse(saved);
    }

    @Transactional(readOnly = true)
    public QuizResultResponse getLatestResult(Long quizId) {
        quizService.findQuizOrThrow(quizId);
        QuizAttempt attempt = quizAttemptRepository
                .findTopByQuiz_IdAndStatusOrderBySubmittedAtDesc(quizId, AttemptStatus.SUBMITTED)
                .orElseThrow(() -> new ResourceNotFoundException("No submitted result found for quiz"));
        return toResultResponse(attempt);
    }

    private QuizResultResponse toResultResponse(QuizAttempt attempt) {
        return QuizResultResponse.builder()
                .attemptId(attempt.getId())
                .quizId(attempt.getQuizId())
                .score(attempt.getScore())
                .totalMarks(attempt.getTotalMarks())
                .percentage(attempt.getPercentage())
                .status(attempt.getStatus())
                .submittedAt(attempt.getSubmittedAt())
                .build();
    }
}
