package com.example.quizapp.service;

import com.example.quizapp.dto.QuestionRequest;
import com.example.quizapp.dto.QuestionResponse;
import com.example.quizapp.dto.QuestionUserResponse;
import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizService quizService;

    @Transactional
    public QuestionResponse addQuestion(Long quizId, QuestionRequest request) {
        Quiz quiz = quizService.findQuizOrThrow(quizId);

        Question question = Question.builder()
                .quiz(quiz)
                .questionText(request.getQuestionText())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .optionD(request.getOptionD())
                .correctAnswer(request.getCorrectAnswer())
                .marks(request.getMarks())
                .build();

        Question saved = questionRepository.save(question);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByQuiz(Long quizId) {
        // Ensures the quiz exists before listing its questions
        quizService.findQuizOrThrow(quizId);

        return questionRepository.findByQuiz_Id(quizId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuestionUserResponse> getQuestionsForUser(Long quizId) {
        return questionRepository.findByQuiz_Id(quizId).stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuestionResponse getQuestion(Long questionId) {
        Question question = findQuestionOrThrow(questionId);
        return toResponse(question);
    }

    @Transactional
    public QuestionResponse updateQuestion(Long questionId, QuestionRequest request) {
        Question question = findQuestionOrThrow(questionId);

        question.setQuestionText(request.getQuestionText());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setMarks(request.getMarks());

        Question saved = questionRepository.save(question);
        return toResponse(saved);
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = findQuestionOrThrow(questionId);
        questionRepository.delete(question);
    }

    @Transactional(readOnly = true)
    public Question findQuestionOrThrow(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
    }

    private QuestionResponse toResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .quizId(question.getQuizId())
                .questionText(question.getQuestionText())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .correctAnswer(question.getCorrectAnswer())
                .marks(question.getMarks())
                .build();
    }

    private QuestionUserResponse toUserResponse(Question question) {
        // correctAnswer is intentionally excluded here
        return QuestionUserResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .build();
    }
}
