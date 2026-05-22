package org.example.tarotpokerapplication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GameSessionCreateDto {

    @NotBlank(message = "userId is required")
    private String userId;
}
