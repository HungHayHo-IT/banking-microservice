# Bước 1: Build ứng dụng sử dụng JDK
FROM eclipse-temurin:21-jdk-alpine AS builder

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Copy wrapper của Maven và file cấu hình (nếu có)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Cấp quyền thực thi cho file mvnw
RUN chmod +x ./mvnw

# Tải trước các dependencies (giúp tăng tốc độ build cho các lần sau)
RUN ./mvnw dependency:go-offline

# Copy toàn bộ mã nguồn vào container
COPY src ./src

# Build dự án và bỏ qua chạy test để tối ưu thời gian
RUN ./mvnw clean package -DskipTests

# Bước 2: Chạy ứng dụng với môi trường JRE siêu nhẹ
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy file .jar đã được build từ bước builder sang
COPY --from=builder /app/target/*.jar app.jar

# Expose cổng (bạn có thể thay đổi tùy theo cổng cấu hình trong application.yml của từng service)
EXPOSE 8081

# Lệnh khởi chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]