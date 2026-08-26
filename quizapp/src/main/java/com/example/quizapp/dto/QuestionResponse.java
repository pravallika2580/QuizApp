package com.example.quizapp.dto;

import lombok.*;

/**
 * Full question response used by ADMIN APIs only.
 * Includes correctAnswer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private Long id;
    private Long quizId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private Integer marks;
}
