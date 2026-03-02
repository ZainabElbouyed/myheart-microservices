package com.myheart.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    
    private String token;
    private String type;
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
}