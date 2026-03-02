package com.example.springproject.model;

public enum GoalDifficulty {
    EASY(10),
    MEDIUM(50),
    HARD(100);

    private final int xpReward;

    GoalDifficulty(int xpReward) {
        this.xpReward = xpReward;
    }

    public int getXpReward() {
        return xpReward;
    }
}
