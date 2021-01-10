package com.example.gameedgeservice.model;

import java.util.ArrayList;
import java.util.List;

public class FilledGamePricesReview {

    private Integer appId;
    private String gameTitle;
    private Prices prices;
    private List<UserScore> userScores;

    public FilledGamePricesReview(Game game, Prices prices, List<Review> reviews) {
        setGameTitle(game.getGameTitle());
        setAppId(game.getAppId());
        setPrices(prices);
        userScores = new ArrayList<>();
        reviews.forEach(review -> {
            userScores.add(new UserScore(review.getUserId(), review.getScoreNumber()));
        });
        setUserScores(userScores);
    }

    public FilledGamePricesReview(Game game, Prices prices, Review review) {
        setGameTitle(game.getGameTitle());
        setAppId(game.getAppId());
        setPrices(prices);
        userScores = new ArrayList<>();
        userScores.add(new UserScore(review.getUserId(), review.getScoreNumber()));
        setUserScores(userScores);
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public Prices getPrices() {
        return prices;
    }

    public void setPrices(Prices prices) {
        this.prices = prices;
    }

    public List<UserScore> getUserScores() {
        return userScores;
    }

    public void setUserScores(List<UserScore> userScores) {
        this.userScores = userScores;
    }
}
