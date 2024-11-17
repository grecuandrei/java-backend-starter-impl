package com.store.application.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
public class AuthRequest {
    private String email;
    @ToString.Exclude
    private String password;
}
