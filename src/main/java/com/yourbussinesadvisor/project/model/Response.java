package com.yourbussinesadvisor.project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    // Constructors, getters, setters
    public Response() {
    }

    public Response(Form form, Long userId, LocalDateTime date) {
        this.form = form;
        this.userId = userId;
        this.date = date;
    }
}
