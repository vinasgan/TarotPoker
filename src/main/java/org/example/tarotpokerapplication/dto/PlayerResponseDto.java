package org.example.tarotpokerapplication.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlayerResponseDto {
    private String id;
    private String name;
    private int wins;
    private List<String> minorCards;
    private List<String> majorCards;
}
