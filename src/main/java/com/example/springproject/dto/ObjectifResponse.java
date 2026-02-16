package com.example.springproject.dto;

import com.example.springproject.model.MoodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectifResponse {

    private Long id;
    private LocalDate objectifDate;
    private MoodType mood;
    private Boolean consumed;
    private String notes;
    private LocalDateTime createdAt;
}
