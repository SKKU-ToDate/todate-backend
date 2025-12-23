# ToDate Backend Makefile

.PHONY: help build run stop logs clean

# 기본 타겟: 도움말 표시
help:
	@echo "ToDate Backend 관리 명령어"
	@echo ""
	@echo "  make build   - Gradle 빌드 (JAR 파일 생성)"
	@echo "  make run     - Docker Compose 실행"
	@echo "  make stop    - Docker Compose 중지"
	@echo "  make logs    - 컨테이너 로그 확인"
	@echo "  make clean   - 빌드 아티팩트 및 볼륨 정리"
	@echo ""

# Gradle 빌드
build:
	@echo "Building with Gradle..."
	./gradlew clean build -x test
	@echo "Build complete! JAR: build/libs/*.jar"

# Docker Compose 실행 (로컬)
run: build
	@echo "Starting Docker Compose (local)..."
	docker compose -f docker-compose.yml -f docker-compose.local.yml up -d
	@echo "Services started!"
	@echo "Application: http://localhost:8080"
	@echo "PostgreSQL: localhost:5432"
	@echo "Redis: localhost:6379"
	@echo ""
	@echo "Logs: make logs"

# Docker Compose 중지
stop:
	@echo "Stopping Docker Compose..."
	docker compose -f docker-compose.yml -f docker-compose.local.yml down
	@echo "Stopped."

# 로그 확인
logs:
	docker compose -f docker-compose.yml -f docker-compose.local.yml logs -f

# 전체 정리 (볼륨 포함)
clean:
	@echo "Cleaning up..."
	./gradlew clean
	docker compose -f docker-compose.yml -f docker-compose.local.yml down -v
	@echo "Cleaned."