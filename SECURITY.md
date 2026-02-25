# Security

## Do not commit secrets
- Never commit `.env` or any API keys.
- Use `.env.example` as a template and keep real values only on your machine / secret manager.

## If a key was exposed
1. Revoke it immediately in the provider dashboard.
2. Rotate it (create a new key).
3. Remove the secret from git history if it was ever pushed.
