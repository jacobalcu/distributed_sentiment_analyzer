package com.alcumbrack.sentiment_crawler;

import com.alcumbrack.sentiment_crawler.scraper.RedditScraper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SentimentCrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SentimentCrawlerApplication.class, args);
	}

	@Bean // Runs automatically on start
	CommandLineRunner runScraper(RedditScraper scraper) {
		return args -> {
			System.out.println("--- Starting Scraper ---");
			scraper.scrapeSubreddit("java");

//			titles.forEach(title -> System.out.println("Found: " + title));

			System.out.println(("--- Scraper Finished ---"));
		};
	}

}
