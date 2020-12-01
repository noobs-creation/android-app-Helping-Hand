package com.example.helpinghand;

public class NewsFeed {
    private String details;
    private String imgLink;

    public NewsFeed(String details, String imgLink) {
        this.details = details;
        this.imgLink = imgLink;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public NewsFeed() {
    }
}
