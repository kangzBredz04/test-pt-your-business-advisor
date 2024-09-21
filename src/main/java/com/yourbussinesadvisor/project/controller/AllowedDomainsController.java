package com.yourbussinesadvisor.project.controller;

import com.yourbussinesadvisor.project.model.AllowedDomain;
import com.yourbussinesadvisor.project.model.Form;
import com.yourbussinesadvisor.project.repository.AllowedDomainsRepository;
import com.yourbussinesadvisor.project.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/allowed-domains")
@RequiredArgsConstructor
public class AllowedDomainsController {

    private final AllowedDomainsRepository allowedDomainsRepository;
    private final FormRepository formRepository;

    @PostMapping
    public ResponseEntity<?> createAllowedDomain(@RequestParam Long formId, @RequestBody AllowedDomain allowedDomain) {
        // Find the form by ID
        Optional<Form> formOptional = formRepository.findById(formId);
        if (!formOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Form not found");
        }

        // Set the form for the allowed domain
        allowedDomain.setForm(formOptional.get());

        // Save the allowed domain
        allowedDomainsRepository.save(allowedDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(allowedDomain);
    }

    @GetMapping("/form/{formId}")
    public ResponseEntity<?> getAllowedDomainsByFormId(@PathVariable Long formId) {
        // Fetch allowed domains by form ID
        List<AllowedDomain> allowedDomains = allowedDomainsRepository.findByFormId(formId);
        if (allowedDomains.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No allowed domains found for this form");
        }
        return ResponseEntity.ok(allowedDomains);
    }
}
