package com.yourbussinesadvisor.project.controller;

import com.yourbussinesadvisor.project.dto.FormRequest;
import com.yourbussinesadvisor.project.model.AllowedDomain;
import com.yourbussinesadvisor.project.model.Form;
import com.yourbussinesadvisor.project.model.User;
import com.yourbussinesadvisor.project.repository.AllowedDomainsRepository;
import com.yourbussinesadvisor.project.repository.FormRepository;
import com.yourbussinesadvisor.project.repository.QuestionRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/forms")
@RequiredArgsConstructor
public class FormController {

    private final FormRepository formRepository;
    private final AllowedDomainsRepository allowedDomainsRepository;
    private final QuestionRepository questionRepository;

    @PostMapping
    public ResponseEntity<?> createForm(@Valid @RequestBody FormRequest formRequest) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthenticated."));
        }

        // Check for existing slug
        if (formRepository.existsBySlug(formRequest.getSlug())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid field",
                    "errors", Map.of("slug", List.of("The slug has already been taken."))));
        }

        // Get the creator_id from the authenticated user
        User user = (User) authentication.getPrincipal();
        Long creatorId = user.getId();

        // Create and save the form
        Form newForm = new Form();
        newForm.setName(formRequest.getName());
        newForm.setSlug(formRequest.getSlug());
        newForm.setDescription(formRequest.getDescription());
        newForm.setLimitOneResponse(formRequest.isLimit_one_response());
        newForm.setCreatorId(creatorId);

        // Save the form to get the form ID
        Form savedForm = formRepository.save(newForm);

        // Handle allowed domains
        List<AllowedDomain> allowedDomains = formRequest.getAllowed_domains().stream()
                .map(domain -> {
                    AllowedDomain allowedDomain = new AllowedDomain();
                    allowedDomain.setDomain(domain);
                    allowedDomain.setForm(savedForm); // Set the form reference
                    return allowedDomain;
                }).collect(Collectors.toList());

        // Save allowed domains
        allowedDomainsRepository.saveAll(allowedDomains);

        // Create response object
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Create form success");

        // Create form details
        Map<String, Object> formDetails = new HashMap<>();
        formDetails.put("name", savedForm.getName());
        formDetails.put("slug", savedForm.getSlug());
        formDetails.put("description", savedForm.getDescription());
        formDetails.put("limit_one_response", savedForm.isLimitOneResponse());
        formDetails.put("creator_id", savedForm.getCreatorId());
        formDetails.put("id", savedForm.getId());

        response.put("form", formDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllForms() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthenticated."));
        }

        // Get all forms
        List<Form> forms = formRepository.findAll();

        // Map to desired response structure
        List<Map<String, Object>> formResponses = forms.stream()
                .map(form -> {
                    Map<String, Object> formMap = new HashMap<>();
                    formMap.put("id", form.getId());
                    formMap.put("name", form.getName());
                    formMap.put("slug", form.getSlug());
                    formMap.put("description", form.getDescription());
                    formMap.put("limit_one_response", form.isLimitOneResponse() ? 1 : 0);
                    formMap.put("creator_id", form.getCreatorId());
                    return formMap;
                })
                .collect(Collectors.toList());

        // Create response object
        Map<String, Object> response = Map.of(
                "message", "Get all forms success",
                "forms", formResponses);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{form_slug}")
    public ResponseEntity<?> getFormDetail(@PathVariable String form_slug) {
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
                    .body(Map.of("message", "Form not found"));
        }

        // Get the allowed domains
        List<String> allowedDomains = allowedDomainsRepository.findByFormId(form.getId())
                .stream().map(AllowedDomain::getDomain).collect(Collectors.toList());

        // Get the questions for the form
        List<Map<String, Object>> questions = questionRepository.findByFormId(form.getId())
                .stream().map(question -> {
                    Map<String, Object> questionMap = new HashMap<>();
                    questionMap.put("id", question.getId());
                    questionMap.put("form_id", question.getForm().getId());
                    questionMap.put("name", question.getName());
                    questionMap.put("choice_type", question.getChoiceType().name().toLowerCase().replace("_", " "));
                    questionMap.put("choices", question.getChoices());
                    questionMap.put("is_required", question.getIsRequired() ? 1 : 0);
                    return questionMap;
                })
                .collect(Collectors.toList());

        // Build the response body
        Map<String, Object> response = Map.of(
                "message", "Get form success",
                "form", Map.of(
                        "id", form.getId(),
                        "name", form.getName(),
                        "slug", form.getSlug(),
                        "description", form.getDescription(),
                        "limit_one_response", form.isLimitOneResponse(),
                        "creator_id", form.getCreatorId(),
                        "allowed_domains", allowedDomains,
                        "questions", questions));

        return ResponseEntity.ok(response);
    }

}
