package com.alcumbrack.sentiment_crawler.scraper;

import com.alcumbrack.sentiment_crawler.model.RedditPost;
import com.alcumbrack.sentiment_crawler.repository.RedditPostRepository;
import com.alcumbrack.sentiment_crawler.service.SentimentAnalyzer;
import org.springframework.scheduling.annotation.Scheduled;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service // Tells Spring to manage the class
public class RedditScraper {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper; // Jackson library for JSON parsing
    private final RedditPostRepository repository;
    private final SentimentAnalyzer sentimentAnalyzer;

    // Constructor injection (Spring auto passes the repo in)
    public RedditScraper(RedditPostRepository repository, SentimentAnalyzer sentimentAnalyzer) {
        this.repository = repository;
        this.sentimentAnalyzer = sentimentAnalyzer;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // Run every 60,000 ms (1 minute)
    @Scheduled(fixedRate = 60000)
    public void runScraper() {
        // Scrape a few subreddits
        String[] subreddits = {"java", "programming", "technology"};

        for (String sub : subreddits) {
            scrapeSubreddit(sub);
        }
    }

    // Only called internally so change to private
    private void scrapeSubreddit(String subreddit) {
        String url = "https://www.reddit.com/r/" + subreddit + "/top.json?limit=10";

        try {
            // Build Request in native Java
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Java/Bot 1.0") // Reddit requires User-Agent
                    .GET()
                    .build();

            // Send Request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Error: " + response.statusCode());
                return;
            }

            // Parse JSON
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode posts = rootNode.path("data").path("children");

            if (posts.isArray()) {
                for (JsonNode post : posts) {
                    JsonNode data = post.path("data");

                    // Extract data
                    String title = data.path("title").asText();
                    String author = data.path("author").asText();
                    String postUrl = data.path("url").asText();
                    String content = data.path("selftext").asText();

                    // Defensive check (good for logs)
                    if (title.length() > 255) {
                        System.out.println("Warning: Found a massive title (" + title.length() + " chars: " + title.substring(0, 50) + "...");
                    }

                    // Check for duplicates
                    if (repository.existsByUrl(postUrl)) {
                        System.out.println("Skipping duplicate: " + title);
                        continue; // Jump to next iter of loop
                    }

                    // Analyze title + content combined for better context
                    int score = sentimentAnalyzer.analyze(title + ". " + content);

                    // Create Entity
                    RedditPost newPost = new RedditPost(title, author, postUrl, content);
                    newPost.setSentimentScore(score);

                    // Save to DB
                    repository.save(newPost);

                    System.out.println("Saved: [" + score + "] " + title);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
