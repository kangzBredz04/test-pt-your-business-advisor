package com.yourbussinesadvisor.project.controller;

import com.yourbussinesadvisor.project.model.Answer;
import com.yourbussinesadvisor.project.model.Form;
import com.yourbussinesadvisor.project.model.Question;
import com.yourbussinesadvisor.project.model.Response;
import com.yourbussinesadvisor.project.model.User;
import com.yourbussinesadvisor.project.repository.AnswerRepository;
import com.yourbussinesadvisor.project.repository.FormRepository;
import com.yourbussinesadvisor.project.repository.QuestionRepository;
import com.yourbussinesadvisor.project.repository.ResponseRepository;
import com.yourbussinesadvisor.project.service.FormService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

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

        @Autowired
        private FormService formService;

        @PostMapping("/{form_slug}/responses")
        public ResponseEntity<?> submitResponse(@PathVariable String form_slug,
                        @RequestBody Map<String, Object> requestBody) {

                var authentication = SecurityContextHolder.getContext().getAuthentication();

                // Check if the user is authenticated
                if (authentication == null || authentication.getPrincipal() instanceof String) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(Map.of("error", "Unauthenticated."));
                }

                // Temukan form berdasarkan slug
                Form form = formRepository.findBySlug(form_slug);
                if (form == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(Map.of("message", "Form not found"));
                }

                // Ambil user dari konteks keamanan
                User user = (User) authentication.getPrincipal(); // Pastikan ini mengembalikan objek User

                // Validasi array answers
                List<Map<String, Object>> answers = (List<Map<String, Object>>) requestBody.get("answers");
                if (answers == null || answers.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(Map.of("message", "Answers array is required"));
                }

                // Buat Response baru
                Response response = new Response();
                response.setForm(form);
                response.setUser(user); // Set user yang sedang login
                response.setDate(LocalDateTime.now()); // Set tanggal saat ini
                responseRepository.save(response); // Simpan Response terlebih dahulu

                // Proses setiap jawaban
                for (Map<String, Object> answerData : answers) {
                        Long questionId = ((Number) answerData.get("question_id")).longValue();
                        String value = (String) answerData.get("value");

                        // Temukan pertanyaan berdasarkan ID
                        Optional<Question> questionOptional = questionRepository.findById(questionId);
                        if (questionOptional.isEmpty()) {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(Map.of("message", "Question not found"));
                        }

                        Question question = questionOptional.get();

                        // Periksa jika pertanyaan wajib dan jawabannya kosong
                        if (question.getIsRequired() && (value == null || value.isEmpty())) {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body(Map.of("message", "Answer is required for question: "
                                                                + question.getName()));
                        }

                        // Simpan jawaban
                        Answer answer = new Answer();
                        answer.setResponse(response); // Set response yang baru dibuat
                        answer.setQuestion(question); // Set pertanyaan
                        answer.setValue(value); // Set nilai dari jawaban
                        answerRepository.save(answer); // Simpan jawaban
                }

                return ResponseEntity.status(HttpStatus.OK)
                                .body(Map.of("message", "Submit response success"));
        }

        @GetMapping("/{formSlug}/responses")
        public ResponseEntity<Map<String, Object>> getFormResponses(@PathVariable String formSlug) {
                List<Map<String, Object>> responses = formService.getFormResponses(formSlug);

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("message", "Get responses success");
                responseBody.put("responses", responses);

                return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
}
