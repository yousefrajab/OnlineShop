package com.example.onlineshop.Domain;

public class BannerModel {
    private String url;
    public BannerModel(String url) {
        this.url = url;
    }

    public BannerModel() {

    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
