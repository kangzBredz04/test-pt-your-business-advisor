package com.yourbussinesadvisor.project.service;

import java.security.SecureRandom;
import java.text.ParseException;

import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class JwtService {

    private static byte[] secretKey = new byte[32];

    public JwtService() {
        new SecureRandom().nextBytes(secretKey);
    }

    public String create(String payload) throws JOSEException {
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256),
                new Payload(payload));
        JWSSigner signer = new MACSigner(secretKey);
        jwsObject.sign(signer);
        return jwsObject.serialize();
    }

    public String verify(String token) throws ParseException, JOSEException {
        JWSObject jwsObject = JWSObject.parse(token);
        JWSVerifier verifier = new MACVerifier(secretKey);
        if (jwsObject.verify(verifier)) {
            return jwsObject.getPayload().toString();
        } else {
            return null;
        }
    }

    public void signOut(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
