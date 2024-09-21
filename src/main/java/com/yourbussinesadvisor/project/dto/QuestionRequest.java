package com.yourbussinesadvisor.project.dto;

import lombok.Data;

import java.util.List;

import com.yourbussinesadvisor.project.model.ChoiceType;

@Data
public class QuestionRequest {
    private String name;
    private ChoiceType choiceType;
    private List<String> choices; // can be null for types that do not require choices
    private Boolean isRequired;
}
