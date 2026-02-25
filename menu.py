#!/usr/bin/env python3
"""
Docker Management Menu for Spring AI Ollama Application
"""
\1
def ensure_env_file():
    """Warn if .env is missing (keys should live there, not in source)"""
    env_path = os.path.join(os.path.dirname(__file__), ".env")
    example_path = os.path.join(os.path.dirname(__file__), ".env.example")
    if not os.path.exists(env_path):
        print("
⚠️  .env file not found.")
        print("   - Copy .env.example to .env and set values if needed.")
        print("   - Never commit .env to git.
")


def run_command(command, shell=True):
    """Execute a shell command and display output"""
    try:
        print(f"\n🔄 Running: {command}\n")
        result = subprocess.run(command, shell=shell, text=True, capture_output=False)
        return result.returncode == 0
    except Exception as e:
        print(f"❌ Error: {e}")
        return False

def clear_screen():
    """Clear the terminal screen"""
    os.system('cls' if os.name == 'nt' else 'clear')

def show_menu():
    ensure_env_file()
    """Display the main menu"""
    print("\n" + "="*60)
    print("🐳 SPRING AI OLLAMA - DOCKER MANAGEMENT MENU")
    print("="*60)
    print("\n📦 Container Management:")
    print("  1. Start all services (docker compose up -d)")
    print("  2. Stop all services (docker compose down)")
    print("  3. Restart all services")
    print("  4. Rebuild and restart services")
    print("\n🤖 AI Model Management:")
    print("  5. Download/Run AI model (tinyllama)")
    print("  6. List available Ollama models")
    print("\n📊 Monitoring:")
    print("  7. View logs (all services)")
    print("  8. View Spring Boot app logs only")
    print("  9. View Ollama logs only")
    print(" 10. View Frontend logs only")
    print(" 11. Check container status")
    print("\n🧪 Testing:")
    print(" 12. Test API endpoint")
    print(" 13. Open Frontend in browser")
    print("\n🛠️  Maintenance:")
    print(" 14. Clean up stopped containers")
    print(" 15. View disk usage")
    print(" 16. Remove all (containers, volumes, images)")
    print("\n 0. Exit")
    print("="*60)

def start_services():
    """Start all Docker services"""
    success = run_command("docker compose up -d")
    if success:
        print("\n✅ Services started successfully!")
        print("⏳ Waiting for services to be ready (this may take a minute)...")
        import time
        import subprocess
        
        # Wait up to 60 seconds for frontend to be ready
        max_wait = 60
        waited = 0
        while waited < max_wait:
            time.sleep(5)
            waited += 5
            # Check if frontend container is running
            result = subprocess.run(
                ["docker", "inspect", "-f", "{{.State.Running}}", "spring-ai-frontend"],
                capture_output=True,
                text=True,
                shell=True
            )
            if result.returncode == 0 and "true" in result.stdout:
                print(f"\n✅ Services are ready! (waited {waited}s)")
                print("🌐 Opening frontend in browser...")
                time.sleep(2)  # Small delay before opening browser
                open_browser()
                break
            elif waited % 15 == 0:
                print(f"   Still waiting... ({waited}s elapsed)")
        
        if waited >= max_wait:
            print(f"\n⚠️ Services may still be starting. Opening browser anyway...")
            open_browser()
    return success

def stop_services():
    """Stop all Docker services"""
    return run_command("docker compose down")

def restart_services():
    """Restart all Docker services"""
    run_command("docker compose restart")

def rebuild_services():
    """Rebuild and restart services"""
    return run_command("docker compose up -d --build")

def download_model():
    """Download and run the AI model"""
    print("\n⚠️  This will enter interactive mode. Type '/bye' to exit.\n")
    input("Press Enter to continue...")
    run_command("docker exec -it ollama ollama run tinyllama")

def list_models():
    """List available Ollama models"""
    return run_command("docker exec ollama ollama list")

def view_logs():
    """View logs for all services"""
    print("\n📋 Press Ctrl+C to stop viewing logs\n")
    run_command("docker compose logs -f")

def view_spring_logs():
    """View Spring Boot app logs"""
    print("\n📋 Press Ctrl+C to stop viewing logs\n")
    run_command("docker compose logs -f spring-ai-app")

def view_ollama_logs():
    """View Ollama logs"""
    print("\n📋 Press Ctrl+C to stop viewing logs\n")
    run_command("docker compose logs -f ollama")

def view_frontend_logs():
    """View Frontend logs"""
    print("\n📋 Press Ctrl+C to stop viewing logs\n")
    run_command("docker compose logs -f frontend")

def check_status():
    """Check container status"""
    run_command("docker compose ps")
    print("\n")
    run_command("docker stats --no-stream")

def test_api():
    """Test the API endpoint"""
    print("\n🧪 Testing API endpoint...\n")
    try:
        if sys.platform == 'win32':
            run_command('curl "http://localhost:9292/openai/api/chat?message=Hello" -UseBasicParsing')
        else:
            run_command('curl "http://localhost:9292/openai/api/chat?message=Hello"')
    except Exception as e:
        print(f"❌ Error: {e}")
        print("❌ Make sure curl is installed or services are running")

def open_browser():
    """Open Frontend in browser"""
    url = "http://localhost"
    print(f"\n🌐 Opening: {url}\n")
    if sys.platform == 'win32':
        run_command(f'start {url}')
    elif sys.platform == 'darwin':
        run_command(f'open {url}')
    else:
        run_command(f'xdg-open {url}')

def cleanup():
    """Clean up stopped containers"""
    run_command("docker container prune -f")

def disk_usage():
    """View Docker disk usage"""
    run_command("docker system df")

def remove_all():
    """Remove all containers, volumes, and images"""
    print("\n⚠️  WARNING: This will remove ALL containers, volumes, and images!")
    confirm = input("Type 'yes' to confirm: ")
    if confirm.lower() == 'yes':
        run_command("docker compose down -v --rmi all")
        print("\n✅ All resources removed")
    else:
        print("\n❌ Operation cancelled")

def main():
    """Main menu loop"""
    while True:
        try:
            show_menu()
            choice = input("\n👉 Enter your choice (0-16): ").strip()
            
            if choice == '0':
                print("\n👋 Goodbye!\n")
                sys.exit(0)
            elif choice == '1':
                start_services()
            elif choice == '2':
                stop_services()
            elif choice == '3':
                restart_services()
            elif choice == '4':
                rebuild_services()
            elif choice == '5':
                download_model()
            elif choice == '6':
                list_models()
            elif choice == '7':
                view_logs()
            elif choice == '8':
                view_spring_logs()
            elif choice == '9':
                view_ollama_logs()
            elif choice == '10':
                view_frontend_logs()
            elif choice == '11':
                check_status()
            elif choice == '12':
                test_api()
            elif choice == '13':
                open_browser()
            elif choice == '14':
                cleanup()
            elif choice == '15':
                disk_usage()
            elif choice == '16':
                remove_all()
            else:
                print("\n❌ Invalid choice! Please enter a number between 0-16.")
            
            input("\n⏎ Press Enter to continue...")
            
        except KeyboardInterrupt:
            print("\n\n👋 Goodbye!\n")
            sys.exit(0)
        except Exception as e:
            print(f"\n❌ Error: {e}")
            input("\n⏎ Press Enter to continue...")

if __name__ == "__main__":
    main()
