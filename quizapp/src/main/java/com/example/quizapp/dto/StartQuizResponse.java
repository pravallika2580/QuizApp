package com.example.quizapp.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartQuizResponse {

    private Long attemptId;
    private Long quizId;
    private LocalDateTime startedAt;
    private Integer duration;
}
