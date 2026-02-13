# Distributed Sentiment Analyzer
A Full-Stack Java application that crawls Reddit data and performs Sentiment Analysis using Stanford CoreNLP.

## 🛠 Tech Stack
- **Backend:** Java 25, Spring Boot 4.0, Spring Data JPA
- **Database:** PostgreSQL
- **NLP Engine:** Stanford CoreNLP (RNN-based sentiment models)
- **Build Tool:** Maven

## 🚀 Key Features
- **Multi-threaded Scraper:** Efficiently fetches data from Reddit's JSON API.
- **Natural Language Processing:** Grades text on a 5-point scale (Very Negative to Very Positive).
- **Automated Schema Generation:** Uses Hibernate to map Java Entities to SQL.

## 📈 Future Roadmap
- [ ] Implement RabbitMQ for distributed worker processing.
- [ ] Add a React-based visualization dashboard.
- [ ] Integrate a custom PyTorch Computer Vision model for image-based sentiment.