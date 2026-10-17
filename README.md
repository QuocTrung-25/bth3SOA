# Bài thực hành 3 — Product Service

Dịch vụ REST quản lý sản phẩm được xây dựng bằng Java 21 và Spring Boot 3.4.2. Ứng dụng tích hợp xác thực JWT và giao diện quản lý trong một tiến trình tại `http://localhost:8081`. Ứng dụng đọc tài khoản trong bảng `user` và quản lý sản phẩm trong bảng `products` của database MySQL/MariaDB `user`.

## Chạy và dùng thử trên web

1. Cài Java 21, Maven và MariaDB.
2. Database `user` phải có bảng `user` tài khoản và bảng `products` theo dump đã cung cấp. Có thể nạp/cập nhật 5 dòng sản phẩm mẫu bằng task **Initialize products in user database** trong VS Code hoặc bằng CLI:

   ```bash
   mariadb -u root -p < database/product_service.sql
   ```

3. Cấu hình `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` nếu thông tin MySQL/MariaDB khác mặc định. Mặc định URL là `jdbc:mariadb://localhost:3306/user`, username `root`, password rỗng. Có thể cấu hình `JWT_SECRET` (tối thiểu 32 byte, mặc định tương thích với Bth2) và `JWT_EXPIRATION_MS` (mặc định 1 giờ).
4. Khởi động Product Service bằng task **Run Product Service web** hoặc:

   ```bash
   mvn spring-boot:run
   ```

   Trên Windows nếu có Maven Wrapper: `.\mvnw.cmd spring-boot:run`.
5. Mở `http://localhost:8081` và đăng nhập bằng tài khoản CSDL mẫu `Admin` / `123456` (username phân biệt chữ hoa/thường). Giao diện cho phép xem, thêm, sửa và xóa sản phẩm; không cần chạy Bth2 riêng.

Ứng dụng tự xác thực thông tin với bảng `user`, chấp nhận mật khẩu lưu dạng Base64 hoặc plaintext như Bth2, rồi tạo JWT HS256 có cùng khóa và thời hạn mặc định 1 giờ với Bth2. Mọi API `/api/products` đều yêu cầu header `Authorization: Bearer <token>`. Ứng dụng kiểm tra cấu trúc bảng hiện có khi khởi động (`spring.jpa.hibernate.ddl-auto=validate`), không tự tạo hoặc sửa schema. Script chỉ tạo/nạp bảng `products`, không sửa bảng tài khoản `user`.

## API

Base URL: `http://localhost:8081/api/products`

Đăng nhập qua `POST http://localhost:8081/api/auth/login` với JSON:

```json
{
  "userName": "Admin",
  "password": "123456"
}
```

API kiểm tra tài khoản trong bảng `user` và trả JWT với `tokenType` là `Bearer`. Gửi header `Authorization: Bearer <token>` cho các API sản phẩm. Token mặc định hết hạn sau 1 giờ. Không cần chạy Bth2 riêng.

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
