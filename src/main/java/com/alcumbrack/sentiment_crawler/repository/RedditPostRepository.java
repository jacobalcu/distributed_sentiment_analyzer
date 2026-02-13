package com.alcumbrack.sentiment_crawler.repository;

import com.alcumbrack.sentiment_crawler.model.RedditPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedditPostRepository extends JpaRepository<RedditPost, Long> {
    // Now have full CRUD capabilities
    // .save(), .findall(), .delete(), etc
    // If ever need to find by title, just write
    // List<RedditPost> findByTitleContaining(String keyword);

    // Check if URL already exists in DB. Return true/false
    // Spring Data automatically implements the logic
    boolean existsByUrl(String url);
}
