package com.example.springproject.service;

import com.example.springproject.dto.GameLogRequest;
import com.example.springproject.model.GameLog;
import com.example.springproject.model.User;
import com.example.springproject.repository.GameLogRepository;
import com.example.springproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameLogService {

    private final GameLogRepository gameLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public GameLog saveLog(GameLogRequest request) {
        // Only registered authenticated users can save stats.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = auth.getName(); // Usually email

        User user = userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + currentPrincipalName));

        GameLog log = GameLog.builder()
                .user(user)
                .gameType(request.getGameType())
                .rawData(request.getRawData())
                .derivedState(request.getDerivedState())
                .build();

        return gameLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<GameLog> getUserLogs() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return gameLogRepository.findByUserIdOrderByTimestampDesc(user.getId());
    }
}
