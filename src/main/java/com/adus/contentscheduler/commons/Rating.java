package com.adus.contentscheduler.commons;

public enum Rating {
    LOW(1),
    DEFAULT(2),
    HIGH(3);

    private final int score;

    Rating(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
