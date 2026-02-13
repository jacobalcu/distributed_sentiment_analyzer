package com.alcumbrack.sentiment_crawler.service;

import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class SentimentAnalyzer {

    private StanfordCoreNLP pipeline;

    @PostConstruct // Runs once when app starts
    public void init() {
        // Setup pipeline properties
        Properties props = new Properties();
        // Need: tokenize (split words), ssplit (split sentences), parse (grammar), sentiment (score)
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");

        System.out.println("--- Initializing NLP Pipeline (This may take a moment) ---");
        this.pipeline = new StanfordCoreNLP(props);
        System.out.println("--- NLP Pipeline Ready ---");
    }

    public int analyze(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 2; // Neutral if empty
        }

        // Run the pipeline on the text
        Annotation annotation = pipeline.process(text);

        // Text may have multiple sentences. We'll take the longest one
        int mainSentiment = 2;
        int longestStructure = 0;

        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            // Get sentiment tree (structure of the sentence)
            var tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);

            // Simple heuristic: use sentiment of longest sentence
            int score = RNNCoreAnnotations.getPredictedClass(tree);
            String partText = sentence.toString();

            if (partText.length() > longestStructure) {
                mainSentiment = score;
                longestStructure = partText.length();
            }
        }

        return mainSentiment;
    }
}
