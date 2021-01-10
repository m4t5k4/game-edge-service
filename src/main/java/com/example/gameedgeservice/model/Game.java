package com.example.gameedgeservice.model;

import java.util.Date;

public class Game {
    private Integer appId;
    private String gameTitle;
    private String developer;
    private Date releaseDate;

    public Game() {

    }

    public Game(Integer appId, String gameTitle, String developer, Date releaseDate) {
        this.appId = appId;
        this.gameTitle = gameTitle;
        this.developer = developer;
        this.releaseDate = releaseDate;
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

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}
