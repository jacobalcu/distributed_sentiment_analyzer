package com.alcumbrack.sentiment_crawler.model;

import jakarta.persistence.*; // JPA Standard Interface
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // Tells Hibernate to make table out of this class
@Table(name = "reddit_posts") // Specify exact table name in Postgres
public class RedditPost {

    @Id // Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment (1, 2, 3...)
    private Long id;

    @Column(columnDefinition = "TEXT") // Allow longer titles
    private String title;

    private String author;

    @Column(columnDefinition = "TEXT") // Allow unlimited text in postgres
    private String url;

    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT") // Allow unlimited text in postgres
    private String content;

    @Column(name="sentiment_score")
    private int sentimentScore; // 0 to 4
//    {
//     0: Very Negative
//     1: Negative
//     2: Neutral
//     3: Positive
//     4: Very Positive
//    }

    // Stores stock tickers using simple collection table
    // ["NVDA","AMD"]
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_tickers", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "ticker")
    private List<String> tickers;

    // Constructors
    public RedditPost() {} // JPA requires empty constructor

    public RedditPost(String title, String author, String url, String content) {
        this.title = title;
        this.author = author;
        this.url = url;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        // Default to 2 until analyzed
        this.sentimentScore = 2;
        this.tickers = new ArrayList<>();
    }

    // Getters and Setters (req. for JPA to work)
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) {this.title = title;}
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(int sentimentScore) { this.sentimentScore = sentimentScore; }
    public List<String> getTickers() { return tickers; }
    public void setTickers(List<String> tickers) { this.tickers = tickers; }
}
