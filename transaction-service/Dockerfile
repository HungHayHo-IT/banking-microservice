# Bước 1: Build ứng dụng sử dụng image Maven chính thức
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy file cấu hình pom.xml vào container
COPY pom.xml .

# Tải trước dependencies (giúp tăng tốc độ build cho các lần sau)
RUN mvn dependency:go-offline

# Copy toàn bộ mã nguồn vào container
COPY src ./src

# Build dự án bằng lệnh mvn chuẩn (bỏ qua chạy test để tiết kiệm thời gian)
RUN mvn clean package -DskipTests

# Bước 2: Chạy ứng dụng với môi trường JRE siêu nhẹ
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy file .jar được build từ bước builder sang
COPY --from=builder /app/target/*.jar app.jar

# Expose cổng 8082 (dựa theo cấu hình application.yml của project)
EXPOSE 8082

# Lệnh khởi chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]