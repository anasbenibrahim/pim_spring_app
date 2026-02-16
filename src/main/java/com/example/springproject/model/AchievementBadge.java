package com.example.springproject.model;

/**
 * Badge hebdomadaire basé sur la sobriété et l'humeur.
 * - CHAMPION: 4+ jours abstinent, humeur calme ou heureuse
 * - COURAGEUX: 4+ jours abstinent, humeur anxieuse
 * - REBOND: 4+ jours consommés OU 4+ jours abstinent avec humeur triste
 */
public enum AchievementBadge {
    CHAMPION,   // Meilleur - sobriété + humeur positive
    COURAGEUX,  // Moyen - sobriété + humeur anxieuse
    REBOND      // À améliorer - consommation ou humeur triste
}
