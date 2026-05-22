package org.example.tarotpokerapplication.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerUpdateDto {

    @Min(value = 0, message = "Wins cannot be negative")
    private int wins;

    private List<String> minorCards = new ArrayList<>();

    private List<String> majorCards = new ArrayList<>();
}
