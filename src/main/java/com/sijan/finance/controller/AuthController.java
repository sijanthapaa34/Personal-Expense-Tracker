package com.sijan.finance.controller;

import com.sijan.finance.dto.AuthRequest;
import com.sijan.finance.model.Lion;
import com.sijan.finance.security.JwtTokenProvider;
import com.sijan.finance.service.LionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private LionService lionService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request){
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = tokenProvider.generateToken(request.getUsername());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Lion> register(@RequestBody Lion lion){
        return ResponseEntity.ok(lionService.registerLion(lion));
    }

}
