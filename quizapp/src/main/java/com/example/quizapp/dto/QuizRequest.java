package com.example.quizapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRequest {

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "category is required")
    private String category;

    @NotNull(message = "duration is required")
    @Min(value = 1, message = "duration must be greater than 0")
    private Integer duration;

    @NotNull(message = "totalMarks is required")
    @Min(value = 1, message = "totalMarks must be greater than 0")
    private Integer totalMarks;
}
