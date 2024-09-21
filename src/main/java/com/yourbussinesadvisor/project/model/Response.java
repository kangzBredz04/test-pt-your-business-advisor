package com.yourbussinesadvisor.project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

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

    @ManyToOne // Hubungkan dengan model User
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User yang sedang login

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL) // Tambahkan relasi ke Answer
    private List<Answer> answers;

    // Constructors
    public Response() {
        // Default constructor
    }

    // Constructor dengan parameter form, user, dan date
    public Response(Form form, User user) {
        this.form = form;
        this.user = user; // Menggunakan objek User
        this.date = LocalDateTime.now(); // Set tanggal saat ini
    }

    // Metode untuk membuat Response baru dengan tanggal saat ini
    public static Response createResponse(Form form, User user) {
        Response response = new Response();
        response.setForm(form);
        response.setUser(user);
        response.setDate(LocalDateTime.now());
        return response;
    }
}
