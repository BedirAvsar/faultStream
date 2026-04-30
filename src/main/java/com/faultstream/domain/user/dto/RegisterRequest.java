package com.faultstream.domain.user.dto;

public record RegisterRequest(String fullName, String email, String password, String department) {}
