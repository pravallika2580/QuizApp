package com.example.quizapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private QuizAttempt attempt;

    @Column(nullable = false)
    private Long questionId;

    @Column(nullable = false, length = 1)
    private String selectedAnswer;

    @Column(nullable = false)
    private Integer marksObtained;

    @Transient
    public Long getAttemptId() {
        return attempt != null ? attempt.getId() : null;
    }
}
