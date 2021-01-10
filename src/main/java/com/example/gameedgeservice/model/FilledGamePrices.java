package com.example.gameedgeservice.model;

public class FilledGamePrices {

    private Integer appId;
    private String gameTitle;
    private Prices prices;

    public FilledGamePrices(Game game, Prices prices) {
        setGameTitle(game.getGameTitle());
        setAppId(game.getAppId());
        setPrices(prices);
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
}
