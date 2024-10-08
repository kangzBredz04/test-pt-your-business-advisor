package com.yourbussinesadvisor.project.repository;

import com.yourbussinesadvisor.project.model.Form;
import com.yourbussinesadvisor.project.model.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
    List<Response> findByFormId(Long formId);

    List<Response> findByUserId(Long userId);

    List<Response> findByForm(Form form);

    List<Response> findByFormSlug(String slug);
}
