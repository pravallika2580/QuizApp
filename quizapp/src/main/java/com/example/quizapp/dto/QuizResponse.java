package com.example.quizapp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResponse {

    private Long id;
    private String title;
    private String category;
    private Integer duration;
    private Integer totalMarks;
}
