package com.example.quizapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitQuizRequest {

    @NotEmpty(message = "answers list must not be empty")
    @Valid
    private List<UserAnswerRequest> answers;
}
