# WeatherApp

WeatherApp là một ứng dụng Java được xây dựng theo mô hình Maven.\
Ứng dụng được tổ chức với cấu trúc thư mục chuẩn, hỗ trợ phát triển,
kiểm thử và triển khai dễ dàng.

## 📂 Cấu trúc thư mục

    WeatherApp/
    │── src/
    │   ├── main/
    │   │   ├── java/          # Chứa mã nguồn chính của ứng dụng
    │   │   └── resources/     # Chứa tài nguyên (file cấu hình, UI,...)
    │   │
    │   ├── test/
    │   │   ├── java/          # Chứa mã kiểm thử (unit test, integration test)
    │   │   └── resources/     # Tài nguyên phục vụ kiểm thử
    │
    │── target/                # Thư mục được Maven sinh ra sau khi build
    │   ├── generated-sources/
    │   ├── generated-test-sources/
    │   ├── maven-archiver/
    │   ├── maven-status/
    │   └── WeatherApp-1.0-SNAPSHOT.jar   # File jar sau khi build
    │
    │── pom.xml                # File cấu hình Maven

## 🚀 Cách build và chạy

### Build dự án

``` sh
mvn clean install
```

### Chạy ứng dụng

``` sh
java -jar target/WeatherApp-1.0-SNAPSHOT.jar
```

## 🧪 Chạy kiểm thử

``` sh
mvn test
```

## 📦 Phụ thuộc (Dependencies)

Các thư viện và phiên bản được quản lý trong file `pom.xml`.

## 📖 Ghi chú

-   `target/` là thư mục tự sinh, có thể xoá và Maven sẽ build lại.
-   Nên commit file `pom.xml` và mã nguồn, nhưng bỏ qua `target/` khi
    push Git (sử dụng `.gitignore`).
