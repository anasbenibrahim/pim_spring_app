-- Run this SQL to add missing Goals gamification columns to your PostgreSQL database
-- Execute: psql -U YOUR_USERNAME -d pim_db -f add_goals_columns.sql

ALTER TABLE patients ADD COLUMN IF NOT EXISTS total_xp INTEGER NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS patient_unlocked_badges (
    patient_id BIGINT NOT NULL,
    badge_key VARCHAR(255),
    PRIMARY KEY (patient_id, badge_key),
    CONSTRAINT fk_patient_badges FOREIGN KEY (patient_id) REFERENCES patients(user_id)
);
