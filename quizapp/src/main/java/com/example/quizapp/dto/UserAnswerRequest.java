package com.example.quizapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnswerRequest {

    @NotNull(message = "questionId is required")
    private Long questionId;

    @NotBlank(message = "selectedAnswer is required")
    @Pattern(regexp = "[ABCD]", message = "selectedAnswer must be one of A, B, C, D")
    private String selectedAnswer;
}
