package com.example.quizapp.dto;

import lombok.*;

/**
 * Question response used by USER APIs.
 * correctAnswer is intentionally NOT included.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionUserResponse {

    private Long id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
}
