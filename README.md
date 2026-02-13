# Distributed Sentiment Analyzer

A Full-Stack Java application that crawls Reddit data, performs real-time Sentiment Analysis using Stanford CoreNLP, and visualizes trends on a React dashboard.

## 🛠 Tech Stack
- **Backend:** Java 21, Spring Boot 4.0, Spring Data JPA
- **Database:** PostgreSQL 16
- **NLP Engine:** Stanford CoreNLP (RNN-based sentiment models)
- **Frontend:** React 18, Vite, Recharts, Material UI
- **Infrastructure:** Docker & Docker Compose

## 🚀 Key Features
- **Automated Data Pipeline:** Multi-threaded scraper fetches 1,000+ posts daily from Reddit APIs.
- **Natural Language Processing:** Grades text on a 5-point scale (Very Negative to Very Positive).
- **Idempotency:** Prevents duplicate entries using database constraints and optimistic locking.
- **Interactive Dashboard:** Visualizes sentiment distribution with real-time charts.
- **Dockerized:** Entire stack (App, DB, UI) runs with a single command.

---

## 🏁 Getting Started

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (must be running)
- Git

### 1. Clone the Repository
```bash
git clone [https://github.com/YOUR_USERNAME/distributed-sentiment-analyzer.git](https://github.com/YOUR_USERNAME/distributed-sentiment-analyzer.git)
cd distributed-sentiment-analyzer
```

### 2. Run with Docker (Recommended)
This will spin up the Database, Backend, and Frontend containers automatically.
```bash
docker-compose up --build
```
*Note: The first run may take a few minutes to download the NLP models and Maven dependencies.*

### 3. Access the Application
- **Frontend Dashboard:** [http://localhost:5173](http://localhost:5173)
- **Backend API:** [http://localhost:8080/api/posts](http://localhost:8080/api/posts)

### 4. Stopping the App
To stop the containers and save resources:
```bash
docker-compose down
```

---

## 🔧 Architecture
1. **Scraper Service:** Runs on a scheduled cron job (every 60s) to fetch new posts.
2. **Analysis Service:** Passes text through Stanford CoreNLP pipeline.
3. **Persistence Layer:** Stores structured data in PostgreSQL.
4. **REST API:** Exposes endpoints for the frontend to consume.

## 📈 Future Roadmap
- [ ] Implement RabbitMQ for distributed worker processing.
- [ ] Add historical trend analysis (Sentiment over Time).
- [ ] Integrate a custom PyTorch Computer Vision model for image-based sentiment.

## 🤝 Contributing
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request