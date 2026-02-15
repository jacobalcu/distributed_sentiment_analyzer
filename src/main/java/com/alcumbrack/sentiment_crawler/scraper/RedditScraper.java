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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service // Tells Spring to manage the class
public class RedditScraper {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper; // Jackson library for JSON parsing
    private final RedditPostRepository repository;
    private final SentimentAnalyzer sentimentAnalyzer;

    // A predefined list of stocks we care about (The "Watchlist")
    private static final List<String> WATCHLIST = Arrays.asList(
            "NVDA", "TSLA", "AMD", "AAPL", "GOOG", "MSFT", "AMZN", "META", "GME", "PLTR"
    );

    private List<String> extractTickers(String text) {
        List<String> foundTickers = new ArrayList<>();
        String upperText = text.toUpperCase();

        // Check for cashtags ($NVDA)
        Pattern pattern = Pattern.compile("\\$([A-Z]{2,5})");
        Matcher matcher = pattern.matcher(upperText);
        while (matcher.find()) {
            foundTickers.add(matcher.group(1));
        }

        // Check for watchlist words
        for (String ticker : WATCHLIST) {
            // Use word boundaries (\b) so "AMAZON" doesn't match "AMZN" wrongly
            // but "AMD" matches " AMD "
            if (upperText.matches(".*\\b" + ticker +"\\b.*")) {
                if (!foundTickers.contains(ticker)) {
                    foundTickers.add(ticker);
                }
            }
        }
        return foundTickers;
    }

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
        String[] subreddits = {"stocks", "wallstreetbets", "investing", "options"};

        for (String sub : subreddits) {
            scrapeSubreddit(sub);
        }
    }

    // Only called internally so change to private
    private void scrapeSubreddit(String subreddit) {
        String url = "https://www.reddit.com/r/" + subreddit + "/top.json?limit=100&t=day";

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
//                        System.out.println("Skipping duplicate: " + title);
                        continue; // Jump to next iter of loop
                    }

                    // Analyze title + content combined for better context
                    String fullText = title + " " + content;
                    int score = sentimentAnalyzer.analyze(fullText);

                    // Extract Tickers
                    List<String> tickers = extractTickers(fullText);

                    // Skip posts w/ no tickers
                    if (tickers.isEmpty()) {
                        // Log to know it's working but rejecting for no tickers
                        System.out.println("Skipped (No Ticker): " + title.substring(0, Math.min(title.length(), 40)) + "...");
                        continue;
                    }

                    // Create Entity
                    RedditPost newPost = new RedditPost(title, author, postUrl, content);
                    newPost.setSentimentScore(score);
                    newPost.setTickers(tickers);

                    // Save to DB
                    repository.save(newPost);
                    System.out.println("Saved: " + tickers + " -> [" + score + "] " + title);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
