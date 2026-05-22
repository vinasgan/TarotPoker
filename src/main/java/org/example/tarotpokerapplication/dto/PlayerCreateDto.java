package org.example.tarotpokerapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlayerCreateDto {

    @NotBlank(message = "Telegram ID is required")
    private String id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be 2–50 characters")
    private String name;
}
