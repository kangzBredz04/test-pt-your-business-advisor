package com.yourbussinesadvisor.project.repository;

import com.yourbussinesadvisor.project.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Add any custom query methods if needed
}
