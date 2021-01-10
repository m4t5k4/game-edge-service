package com.example.gameedgeservice.model;

import java.util.ArrayList;
import java.util.List;

public class FilledGameReview {

    private Integer appId;
    private String gameTitle;
    private List<UserScore> userScores;

    public FilledGameReview(Game game, List<Review> reviews) {
        setGameTitle(game.getGameTitle());
        setAppId(game.getAppId());
        userScores = new ArrayList<>();
        reviews.forEach(review -> {
            userScores.add(new UserScore(review.getUserId(), review.getScoreNumber()));
        });
        setUserScores(userScores);
    }

    public FilledGameReview(Game game, Review review) {
        setGameTitle(game.getGameTitle());
        setAppId(game.getAppId());
        userScores = new ArrayList<>();
        userScores.add(new UserScore(review.getUserId(),review.getScoreNumber()));
        setUserScores(userScores);
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public List<UserScore> getUserScores() {
        return userScores;
    }

    public void setUserScores(List<UserScore> userScores) {
        this.userScores = userScores;
    }
}
