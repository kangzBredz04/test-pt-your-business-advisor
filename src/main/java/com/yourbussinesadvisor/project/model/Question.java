package com.yourbussinesadvisor.project.model;

import lombok.Data;

import jakarta.persistence.*;

@Data
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    private String name;

    @Enumerated(EnumType.STRING)
    private ChoiceType choiceType;

    private String choices; // Can be stored as a comma-separated string

    private Boolean isRequired;
}
