# Production notes

This repo is set up to be safe for public GitHub:

- No real API keys in source
- `.env` is ignored; `.env.example` is committed
- `target/` and frontend build artifacts are ignored
- Basic CI builds backend + frontend

## Recommended secret management
- Local dev: `.env`
- Production: environment variables / secret manager (AWS Secrets Manager, Kubernetes Secrets, etc.)

## Running
```bash
# create .env
cp .env.example .env

# start stack
docker compose up -d --build

# pull a model the first time
docker exec -it ollama ollama pull tinyllama
```
