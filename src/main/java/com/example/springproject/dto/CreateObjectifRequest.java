package com.example.springproject.dto;

import com.example.springproject.model.MoodType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateObjectifRequest {

    @NotNull(message = "Date is required")
    private LocalDate objectifDate;

    @NotNull(message = "Mood is required")
    private MoodType mood;

    @NotNull(message = "Consumed status is required")
    private Boolean consumed;

    private String notes;
}
