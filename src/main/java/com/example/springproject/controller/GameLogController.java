package com.example.springproject.controller;

import com.example.springproject.dto.GameLogRequest;
import com.example.springproject.model.GameLog;
import com.example.springproject.service.GameLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game-logs")
@RequiredArgsConstructor
public class GameLogController {

    private final GameLogService gameLogService;

    @PostMapping
    public ResponseEntity<GameLog> saveGameLog(@Valid @RequestBody GameLogRequest request) {
        GameLog savedLog = gameLogService.saveLog(request);
        return ResponseEntity.ok(savedLog);
    }

    @GetMapping
    public ResponseEntity<List<GameLog>> getUserGameLogs() {
        List<GameLog> logs = gameLogService.getUserLogs();
        return ResponseEntity.ok(logs);
    }
}
