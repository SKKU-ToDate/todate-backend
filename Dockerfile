FROM eclipse-temurin:21-jre-jammy

# 타임존 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 작업 디렉토리
WORKDIR /app

# H2 데이터 저장 디렉토리 생성
RUN mkdir -p /app/data

# JAR 파일 복사
COPY ./build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]