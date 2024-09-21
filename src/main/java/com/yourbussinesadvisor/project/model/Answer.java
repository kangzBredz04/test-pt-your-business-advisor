package com.yourbussinesadvisor.project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "response_id", nullable = false)
    private Response response;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "value", nullable = false, columnDefinition = "TEXT")
    private String value;

    // Constructors, getters, setters
    public Answer() {
    }

    public Answer(Response response, Question question, String value) {
        this.response = response;
        this.question = question;
        this.value = value;
    }
}
