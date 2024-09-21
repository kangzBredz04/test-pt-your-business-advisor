package com.yourbussinesadvisor.project.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
public class Form {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String slug;

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AllowedDomain> allowedDomains;

    private String description;
    private boolean limitOneResponse;
    private Long creatorId;

    public List<AllowedDomain> getAllowedDomains() {
        return allowedDomains;
    }

    public void setAllowedDomains(List<AllowedDomain> allowedDomains) {
        this.allowedDomains = allowedDomains;
    }
}
