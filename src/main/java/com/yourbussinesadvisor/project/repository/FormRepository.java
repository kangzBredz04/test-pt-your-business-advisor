package com.yourbussinesadvisor.project.repository;

import com.yourbussinesadvisor.project.model.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    boolean existsBySlug(String slug);
}
