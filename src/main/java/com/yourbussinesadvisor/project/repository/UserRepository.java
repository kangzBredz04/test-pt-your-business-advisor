package com.yourbussinesadvisor.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourbussinesadvisor.project.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}