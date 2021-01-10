package com.example.gameedgeservice.model;

public class Review {

    private String id;
    private Integer userId;
    private Integer appId;
    private Integer scoreNumber;

    public Review() {

    }

    public Review(Integer userId, Integer appId, Integer scoreNumber) {
        this.userId = userId;
        this.appId = appId;
        this.scoreNumber = scoreNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getScoreNumber() {
        return scoreNumber;
    }

    public void setScoreNumber(Integer scoreNumber) {
        this.scoreNumber = scoreNumber;
    }
}
