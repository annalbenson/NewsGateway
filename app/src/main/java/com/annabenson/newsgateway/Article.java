package com.annabenson.newsgateway;

import java.io.Serializable;

/**
 * Created by Anna on 4/10/2018.
 */

public class Article implements Serializable {

    private String author;
    private String title;
    private String description;
    private String urlToImage;
    private String publishedAt; // date

    // EC

    private String urlToArticle;


    public Article(String author, String title, String description, String urlToImage, String publishedAt, String urlToArticle) {
        setAuthor(author); setTitle(title); setDescription(description);
        setUrlToImage(urlToImage); setPublishedAt(publishedAt); setUrlToArticle(urlToArticle);

    }

    public String getAuthor() { return author;}

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getUrlToArticle() {
        return urlToArticle;
    }

    public void setUrlToArticle(String urlToArticle) {
        this.urlToArticle = urlToArticle;
    }
}
