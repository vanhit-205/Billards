# Android Java Frontend Coding Conventions

Tài liệu này định nghĩa các quy chuẩn đặt tên và phong cách lập trình Java dành cho dự án Android **Billards**. AI cần tuân thủ nghiêm ngặt các quy tắc này.

## 1. Java Naming Conventions (Quy chuẩn đặt tên Java)

### Class & Interface
*   **PascalCase**: Tên lớp phải là danh từ.
    *   *Ví dụ:* `ProductAdapter`, `TableFragment`, `PaymentActivity`.
*   **Suffix (Hậu tố)**: Luôn có hậu tố chỉ định loại component.
    *   *Ví dụ:* `...Adapter`, `...Activity`, `...Fragment`, `...Model`.

### Variables & Methods
*   **camelCase**: Sử dụng cho tên biến và tên phương thức.
    *   *Ví dụ:* `productName`, `calculateTotalPrice()`.
*   **Global/Member Variables**: Không sử dụng tiền tố `m`. Sử dụng trực tiếp camelCase.
*   **Constants**: **UPPER_SNAKE_CASE**.
    *   *Ví dụ:* `BASE_URL`, `MAX_QUANTITY`.

## 2. XML Layout Conventions (Quy chuẩn Resource)

### View ID naming
Sử dụng quy tắc: **[Tiền tố loại View][Tên đối tượng]** theo kiểu **camelCase**.

*   `TextView` -> `tv` (Ví dụ: `tvProductName`, `tvProductPrice`)
*   `ImageView` -> `img` hoặc `iv` (Ví dụ: `imgProduct`, `ivAvatar`)
*   `Button` -> `btn` (Ví dụ: `btnOrder`, `btnAdd`)
*   `EditText` -> `edt` (Ví dụ: `edtUserName`)
*   `RecyclerView` -> `rcv` hoặc `rv` (Ví dụ: `rcvProductList`)
*   `LinearLayout/RelativeLayout` -> `layout` (Ví dụ: `layoutContainer`)

### Resource Files
*   **Layout**: `[loại_component]_[tên].xml` (Ví dụ: `activity_main.xml`, `fragment_payment.xml`, `item_product.xml`).
*   **Drawable**: `custom_[tên].xml` hoặc `ic_[tên].xml`.

## 3. Coding Style (Phong cách lập trình)

### UI/Frontend
*   Sử dụng **View Binding** (nếu cấu hình) hoặc `findViewById` truyền thống.
*   Tách biệt logic xử lý sự kiện (Click Listener) ra khỏi hàm `onCreate`.
*   Các chuỗi văn bản (Strings) nên được đặt trong `strings.xml`, tránh hardcode trực tiếp trong XML.

### Activity/Fragment Structure
Cấu trúc code trong một file Java thường theo thứ tự:
1. Khai báo biến (Views, Adapters, Data list).
2. `onCreate` / `onCreateView`.
3. `initViews()`: Ánh xạ View.
4. `initData()`: Khởi tạo dữ liệu, gọi API/Firebase.
5. `initEvents()`: Gán các sự kiện click, listener.

### Modernization
*   Ưu tiên sử dụng **Material Design components** (MaterialButton, CardView).
*   Sử dụng **ConstraintLayout** cho các giao diện phức tạp để tối ưu hiệu năng.

## 4. Firebase/Database Interaction
*   Khi lấy dữ liệu từ Firebase, luôn có kiểm tra `null` và `exists()`.
*   Sử dụng các Model class (POJO) để map dữ liệu từ Firebase.

## 5. Yêu cầu đối với AI (Claude 4.6)
*   Luôn giải thích ngắn gọn các đoạn code vừa tạo.
*   Khi đề xuất code mới, phải kiểm tra các ID trong file XML hiện có để đặt tên biến Java cho khớp.
*   Nếu thấy code cũ có thể tối ưu (ví dụ: dùng `Context` không an toàn, memory leak), hãy đưa ra cảnh báo.
*   Ưu tiên viết code dễ đọc, có comment tiếng Việt ở các bước quan trọng.
