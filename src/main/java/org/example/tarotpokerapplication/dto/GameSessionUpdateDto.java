package org.example.tarotpokerapplication.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GameSessionUpdateDto {

    @NotBlank(message = "userId is required")
    private String userId;

    @Min(value = 0, message = "cardIndex must be >= 0")
    private int cardIndex;
}
