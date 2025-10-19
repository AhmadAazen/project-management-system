package com.pmgmt.dto;

import lombok.Setter;

public class JwtResponseDto {
    @Setter
    private String token;
    private String type = "Bearer";
    @Setter
    private String username;

    public JwtResponseDto() {}

    public JwtResponseDto(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() { return token; }

    public String getType() { return type; }

    public String getUsername() { return username; }

}
