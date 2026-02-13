package com.alcumbrack.sentiment_crawler.controller;

import com.alcumbrack.sentiment_crawler.model.RedditPost;
import com.alcumbrack.sentiment_crawler.repository.RedditPostRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Tells Spring this class handles Web Requests
@RequestMapping("/api/posts") // All URLs will start with /api/posts
@CrossOrigin(origins = "http://localhost:5173") // Allow React (running on port 3000) to access this
public class RedditPostController {

    private final RedditPostRepository repository;

    public RedditPostController(RedditPostRepository repository) {
        this.repository = repository;
    }

    // GET http://localhost:8080/api/posts
    @GetMapping
    public List<RedditPost> getAllPosts() {
        // In real app, use Pagination. For now, get everything
        return repository.findAll();
    }

    // GET http://localhost:8080/api/posts/sentiment/3 (Get all positive posts)
    @GetMapping("/sentiment/{score}")
    public List<RedditPost> getPostsBySentiment(@PathVariable int score) {
        return repository.findBySentimentScore(score);
    }
}
