# Bài thực hành 3 — Product Service

Dịch vụ REST quản lý sản phẩm được xây dựng bằng Java 21 và Spring Boot 3.4.2. Ứng dụng có giao diện web tại `http://localhost:8081` và chạy độc lập trên cổng `8081`. Product Service dùng bảng `products` trong database MySQL/MariaDB `user`, cùng database với bảng tài khoản của Bth2. Trang đăng nhập gọi endpoint `/api/v1/auth/login` của Bth2; password được mã hóa Base64 theo định dạng Bth2 yêu cầu.

## Chạy và dùng thử trên web

1. Cài Java 21, Maven và MariaDB.
2. Chạy ứng dụng Bth2 (endpoint `POST /api/v1/auth/login`) trên cổng `8082`. Mã nguồn Bth2 cục bộ đã được chỉnh `server.port=8082` vì cổng `8080` đang bị Tomcat khác sử dụng. Có thể chạy task **Run Bth2 JWT on port 8082** trong VS Code hoặc, từ thư mục `BTH3SOA`, chạy `mvn -f ..\BaiTh2SOA\pom.xml spring-boot:run`.
3. Database `user` phải có bảng `products` theo schema trong dump đã cung cấp. Có thể nạp/cập nhật 5 dòng dữ liệu mẫu bằng task **Initialize products in user database** trong VS Code hoặc bằng CLI:

   ```bash
   mariadb -u root -p < database/product_service.sql
   ```

4. Cấu hình `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` nếu thông tin MySQL/MariaDB khác mặc định. Mặc định URL là `jdbc:mariadb://localhost:3306/user`, username `root`, password rỗng. Cấu hình địa chỉ xác thực qua `AUTH_SERVICE_URL` (mặc định `http://localhost:8082`) và đổi cổng ứng dụng qua `SERVER_PORT`.
5. Khởi động Product Service bằng task **Run Product Service web** hoặc:

   ```bash
   mvn spring-boot:run
   ```

   Trên Windows nếu có Maven Wrapper: `.\mvnw.cmd spring-boot:run`.
6. Mở `http://localhost:8081` và đăng nhập bằng tài khoản CSDL mẫu `Admin` / `123456` (username phân biệt chữ hoa/thường). Giao diện cho phép xem, thêm, sửa và xóa sản phẩm.

Ứng dụng kiểm tra cấu trúc bảng hiện có lúc khởi động (`spring.jpa.hibernate.ddl-auto=validate`), không tự tạo hoặc sửa schema. `database/product_service.sql` chỉ tạo/nạp bảng `products` trong database `user`; không xóa hay sửa bảng tài khoản `user`.

## API

Base URL: `http://localhost:8081/api/products`

| Method | Endpoint | Chức năng | Thành công |
| --- | --- | --- | --- |
| `GET` | `/api/products` | Lấy danh sách sản phẩm | `200 OK` |
| `GET` | `/api/products/{id}` | Xem một sản phẩm | `200 OK`; `404 Not Found` nếu không có |
| `POST` | `/api/products` | Tạo sản phẩm | `201 Created`, kèm header `Location` |
| `PUT` | `/api/products/{id}` | Cập nhật toàn bộ thông tin sản phẩm | `200 OK`; `404 Not Found` nếu không có |
| `DELETE` | `/api/products/{id}` | Xóa sản phẩm | `204 No Content`; `404 Not Found` nếu không có |

Ví dụ body tạo/cập nhật:

```json
{
  "name": "Bàn phím cơ",
  "description": "Bàn phím Bluetooth",
  "price": 1650000.00,
  "quantity": 20
}
```

`name` không được để trống và tối đa 255 ký tự; `price` phải không âm, tối đa 2 chữ số thập phân; `quantity` phải không âm. `id`, `createdAt` và `updatedAt` được dịch vụ quản lý.
