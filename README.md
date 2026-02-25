
# Spring AI Fullstack Production Project

## Architecture

- Spring Boot Backend (Port 9292)
- React Frontend (Port 3000 via Nginx)
- Dockerized Fullstack Setup
- Secure Environment Variable Handling

## Setup

1. Create `.env` file in root:

OPENAI_API_KEY=your_real_key_here

2. Build & Run:

docker compose up --build

3. Access:

Backend:
http://localhost:9292

Frontend:
http://localhost:3000

## Security

- No API keys committed
- Uses environment variables
- Production-ready Docker setup
