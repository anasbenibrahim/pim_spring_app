package com.example.springproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class GameLogRequest {

    @NotBlank(message = "Game type is required")
    private String gameType;

    @NotNull(message = "Raw data is required")
    private List<Integer> rawData;

    @NotBlank(message = "Derived state is required")
    private String derivedState;
}
