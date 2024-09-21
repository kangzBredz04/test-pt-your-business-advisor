package com.yourbussinesadvisor.project.controller;

import com.yourbussinesadvisor.project.model.Answer;
import com.yourbussinesadvisor.project.model.Form;
import com.yourbussinesadvisor.project.model.Question;
import com.yourbussinesadvisor.project.model.Response;
import com.yourbussinesadvisor.project.repository.AnswerRepository;
import com.yourbussinesadvisor.project.repository.FormRepository;
import com.yourbussinesadvisor.project.repository.QuestionRepository;
import com.yourbussinesadvisor.project.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/forms")
public class ResponseController {

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @PostMapping("/{form_slug}/responses")
    public ResponseEntity<?> submitResponse(@PathVariable String form_slug,
            @RequestBody Map<String, Object> requestBody) {
        // Find the form by slug
        Form form = formRepository.findBySlug(form_slug);
        if (form == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Form not found"));
        }

        // Validate the answers array
        List<Map<String, Object>> answers = (List<Map<String, Object>>) requestBody.get("answers");
        if (answers == null || answers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Answers array is required"));
        }

        // Create a new response for the form
        Response response = new Response();
        response.setForm(form);
        responseRepository.save(response); // Save the response first

        // Process each answer
        for (Map<String, Object> answerData : answers) {
            Long questionId = ((Number) answerData.get("question_id")).longValue();
            String value = (String) answerData.get("value");

            // Find the question by ID
            Optional<Question> questionOptional = questionRepository.findById(questionId);
            if (questionOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Question not found"));
            }

            Question question = questionOptional.get();

            // Check if the question is required and the answer is empty
            if (question.getIsRequired() && (value == null || value.isEmpty())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Answer is required for question: " + question.getName()));
            }

            // Save the answer
            Answer answer = new Answer(response, question, value);
            answerRepository.save(answer);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Submit response success"));
    }

}
