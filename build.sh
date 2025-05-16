#!/bin/bash

echo "🛠️  Build project bằng Maven (tạo file jar)..."
mvn clean package -DskipTests

echo "🛠️  Build Docker image..."
docker build -t my-spring-app .

echo "Dừng và xóa các container hiện tại..."
docker-compose down

echo " Khởi động lại các container bằng docker-compose..."
docker-compose up -d --build

echo "http://localhost:8082/swagger-ui/index.html"
