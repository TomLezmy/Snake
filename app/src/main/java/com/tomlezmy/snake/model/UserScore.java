package com.tomlezmy.snake.model;

import java.io.Serializable;

public class UserScore implements Serializable {
    private String userName;
    private int score;

    public UserScore(String userName, int score) {
        this.userName = userName;
        this.score = score;
    }

    public String getUserName() {
        return userName;
    }

    public int getScore() {
        return score;
    }
}
