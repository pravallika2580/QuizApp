package com.example.quizapp.dto;

import com.example.quizapp.entity.AttemptStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResultResponse {

    private Long attemptId;
    private Long quizId;
    private Integer score;
    private Integer totalMarks;
    private Double percentage;
    private AttemptStatus status;
    private LocalDateTime submittedAt;
}
