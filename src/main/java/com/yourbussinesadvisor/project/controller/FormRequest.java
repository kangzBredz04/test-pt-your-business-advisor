package com.yourbussinesadvisor.project.controller;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class FormRequest {
    @NotBlank(message = "The name field is required.")
    private String name;

    @NotBlank(message = "The slug field is required.")
    @Size(min = 1, message = "The slug cannot be empty.")
    private String slug;

    @NotEmpty(message = "The allowed domains must be an array.")
    private List<String> allowed_domains;

    @NotBlank(message = "The description field is required.")
    private String description;

    private boolean limit_one_response;
}
