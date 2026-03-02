package com.example.springproject.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PseudonymService {

    private static final String[] ADJECTIVES = {
        "Brave", "Calm", "Gentle", "Hopeful", "Kind", 
        "Peaceful", "Strong", "Wise", "Warm", "Bright",
        "Resilient", "Steady", "Fierce", "Loyal", "Solid"
    };

    private static final String[] NOUNS = {
        "Eagle", "Lion", "Tiger", "Bear", "Wolf",
        "Falcon", "Hawk", "Owl", "Phoenix", "Dragon",
        "River", "Mountain", "Forest", "Ocean", "Star"
    };

    private final Random random = new Random();

    public String generatePseudonym() {
        String adj = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[random.nextInt(NOUNS.length)];
        int number = 10 + random.nextInt(90); // 10 to 99
        return adj + noun + "_" + number;
    }
}
