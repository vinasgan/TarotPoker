package org.example.tarotpokerapplication.security.dto;

import org.example.tarotpokerapplication.security.Role;

public record RegisterRequest(String username, String password, Role role) {}
