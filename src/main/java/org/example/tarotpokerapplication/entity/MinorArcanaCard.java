package org.example.tarotpokerapplication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MinorArcanaCard {
    private final String name;
    private final String suit;
    private final int power;

}
