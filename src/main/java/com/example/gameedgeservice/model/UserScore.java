package com.example.gameedgeservice.model;

public class UserScore {
    private Integer userId;
    private Integer scoreNumber;

    public UserScore(Integer userId, Integer scoreNumber) {
        this.userId = userId;
        this.scoreNumber = scoreNumber;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getScoreNumber() {
        return scoreNumber;
    }

    public void setScoreNumber(Integer scoreNumber) {
        this.scoreNumber = scoreNumber;
    }
}
