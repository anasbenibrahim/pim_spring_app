package com.example.springproject.repository;

import com.example.springproject.model.GameLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameLogRepository extends JpaRepository<GameLog, Long> {
    List<GameLog> findByUserIdOrderByTimestampDesc(Long userId);
}
