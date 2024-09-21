package com.yourbussinesadvisor.project.repository;

import com.yourbussinesadvisor.project.model.Form;
import com.yourbussinesadvisor.project.model.Question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByIdAndForm(Long id, Form form);
}
