package org.example.tarotpokerapplication.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerUpdateDto {

    @Min(value = 0, message = "Wins cannot be negative")
    private int wins;

    @NotEmpty(message = "Minor cards cannot be empty")
    private List<String> minorCards;

    private List<String> majorCards = new ArrayList<>();
}
