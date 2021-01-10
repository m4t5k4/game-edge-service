package com.example.gameedgeservice.model;

public class Prices {

    public Long id;
    private Integer appId;
    private double euro;
    private double dollar;
    private double pound;
    private double peso;

    public Prices(){

    }

    public Prices(Integer appId, double euro, double dollar, double pound, double peso) {
        this.appId = appId;
        this.euro = euro;
        this.dollar = dollar;
        this.pound = pound;
        this.peso = peso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public double getEuro() {
        return euro;
    }

    public void setEuro(double euro) {
        this.euro = euro;
    }

    public double getDollar() {
        return dollar;
    }

    public void setDollar(double dollar) {
        this.dollar = dollar;
    }

    public double getPound() {
        return pound;
    }

    public void setPound(double pound) {
        this.pound = pound;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }
}
