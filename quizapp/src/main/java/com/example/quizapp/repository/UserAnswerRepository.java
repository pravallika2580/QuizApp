package com.example.quizapp.repository;

import com.example.quizapp.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    List<UserAnswer> findByAttempt_Id(Long attemptId);

    void deleteByAttempt_Id(Long attemptId);
}
