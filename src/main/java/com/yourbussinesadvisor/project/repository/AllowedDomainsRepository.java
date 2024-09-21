package com.yourbussinesadvisor.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yourbussinesadvisor.project.model.AllowedDomain;

import java.util.List;

@Repository
public interface AllowedDomainsRepository extends JpaRepository<AllowedDomain, Long> {
    // Custom query to find domains by form_id
    List<AllowedDomain> findByFormId(Long formId);
}
