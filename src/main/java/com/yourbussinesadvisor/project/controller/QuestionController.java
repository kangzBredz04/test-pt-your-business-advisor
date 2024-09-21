package com.yourbussinesadvisor.project.controller;

import com.yourbussinesadvisor.project.dto.QuestionRequest;
import com.yourbussinesadvisor.project.model.ChoiceType;
import com.yourbussinesadvisor.project.model.Form;
import com.yourbussinesadvisor.project.model.Question;
import com.yourbussinesadvisor.project.repository.FormRepository;
import com.yourbussinesadvisor.project.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/forms")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final FormRepository formRepository;

    @PostMapping("/{form_slug}/questions")
    public ResponseEntity<?> addQuestion(@PathVariable String form_slug, @RequestBody QuestionRequest questionRequest) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthenticated."));
        }

        // Find the form by slug
        Form form = formRepository.findBySlug(form_slug);
        if (form == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Form not found."));
        }

        // Validate choices based on choiceType
        if ((questionRequest.getChoiceType() == ChoiceType.MULTIPLE_CHOICE ||
                questionRequest.getChoiceType() == ChoiceType.DROPDOWN ||
                questionRequest.getChoiceType() == ChoiceType.CHECKBOXES) &&
                (questionRequest.getChoices() == null || questionRequest.getChoices().isEmpty())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid field",
                    "errors", Map.of("choices", List.of("Choices must be provided for this type."))));
        }

        // Create the question
        System.out.println(questionRequest);
        Question question = new Question();
        question.setName(questionRequest.getName());
        question.setChoiceType(questionRequest.getChoiceType());
        question.setChoices(
                questionRequest.getChoices() != null ? String.join(",", questionRequest.getChoices()) : null);
        question.setIsRequired(questionRequest.getIsRequired());
        question.setForm(form); // Set the form reference

        // Save the question
        Question savedQuestion = questionRepository.save(question);

        // Create response object
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Question added successfully.");
        response.put("question", savedQuestion);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
