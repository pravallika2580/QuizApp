package com.example.quizapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRequest {

    @NotBlank(message = "questionText is required")
    private String questionText;

    @NotBlank(message = "optionA is required")
    private String optionA;

    @NotBlank(message = "optionB is required")
    private String optionB;

    @NotBlank(message = "optionC is required")
    private String optionC;

    @NotBlank(message = "optionD is required")
    private String optionD;

    @NotBlank(message = "correctAnswer is required")
    @Pattern(regexp = "[ABCD]", message = "correctAnswer must be one of A, B, C, D")
    private String correctAnswer;

    @NotNull(message = "marks is required")
    @Min(value = 1, message = "marks must be greater than 0")
    private Integer marks;
}
