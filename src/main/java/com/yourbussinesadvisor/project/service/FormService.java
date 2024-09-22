package com.yourbussinesadvisor.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourbussinesadvisor.project.model.Answer;
import com.yourbussinesadvisor.project.model.Response;
import com.yourbussinesadvisor.project.repository.ResponseRepository;

import java.util.*;

@Service
public class FormService {

    @Autowired
    private ResponseRepository responseRepository;

    public List<Map<String, Object>> getFormResponses(String formSlug) {
        List<Response> responses = responseRepository.findByFormSlug(formSlug);

        // Konversi data menjadi JSON yang diinginkan
        List<Map<String, Object>> responseList = new ArrayList<>();

        for (Response response : responses) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("date", response.getDate().toString());

            // Ambil user data
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", response.getUser().getId());
            userData.put("name", response.getUser().getName());
            userData.put("email", response.getUser().getEmail());
            userData.put("email_verified_at", response.getUser().getEmailVerifiedAt());

            responseData.put("user", userData);

            // Ambil jawaban untuk respon ini
            Map<String, String> answersMap = new HashMap<>();
            for (Answer answer : response.getAnswers()) {
                answersMap.put(answer.getQuestion().getName(), answer.getValue());
            }
            responseData.put("answers", answersMap);

            responseList.add(responseData);
        }

        return responseList;
    }
}
