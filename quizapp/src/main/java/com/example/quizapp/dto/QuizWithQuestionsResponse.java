package com.example.quizapp.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizWithQuestionsResponse {

    private Long id;
    private String title;
    private String category;
    private Integer duration;
    private Integer totalMarks;
    private List<QuestionUserResponse> questions;
}
