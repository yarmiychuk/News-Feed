package com.yarmiychuk.newsfeed;

/**
 * Created by DmitryYarmiychuk on 24.06.2018.
 * Создал DmitryYarmiychuk 24.06.2018
 */

public class NewsItem {

    private String date, title, description, author, webLink;

    // Constructor
    NewsItem(String date, String title, String description, String author, String webLink) {
        this.date = date;                   // Date of publication
        this.title = title;                 // Title
        this.description = description;     // Short description
        this.author = author;               // Author
        this.webLink = webLink;             // Link to publication
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getWebLink() {
        return webLink;
    }
}
