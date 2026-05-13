# Thiết kế Màn hình Admin (AdminActivity & Fragments)

## 1. Cấu trúc tổng thể
- **Activity chính:** `AdminActivity` chứa một `BottomNavigationView` hoặc `TabLayout` để chuyển đổi giữa 2 Fragment.
- **Fragment 1:** `StaffManagementFragment` (Quản lý nhân viên).
- **Fragment 2:** `RevenueStatisticsFragment` (Thống kê doanh thu).

---

## 2. Fragment: Quản lý nhân viên (StaffManagementFragment)

### A. Giao diện (UI)
- **Danh sách:** `RecyclerView` để hiển thị thẻ (CardView) thông tin từng nhân viên (Tên, Email).
- **Nút thêm:** `FloatingActionButton` (FAB) ở góc dưới bên phải để mở Dialog thêm nhân viên.
- **Thao tác xóa:** Vuốt thẻ nhân viên (Swipe to delete) hoặc một nút "Thùng rác" nhỏ trên mỗi thẻ.

### B. Logic xử lý
- **Hiển thị:** Sử dụng `FirebaseRecyclerAdapter` để tự động cập nhật danh sách khi Firestore thay đổi.
- **Thêm nhân viên:**
    1. Hiển thị `AlertDialog` yêu cầu nhập: Tên, Email, Mật khẩu.
    2. Gọi `mAuth.createUserWithEmailAndPassword`.
    3. Sau khi thành công, lấy `UID` và lưu thông tin vào Collection `users` với `role: "staff"`.
- **Xóa nhân viên:**
    1. Hiển thị xác nhận: "Bạn có chắc muốn xóa nhân viên này?".
    2. Thực hiện `db.collection("users").document(uid).delete()`.
       *(Lưu ý: Xóa trên Firestore không tự xóa trong Firebase Auth, bạn cần dùng Firebase Admin SDK nếu muốn xóa triệt để, nhưng ở mức MVP chỉ cần xóa trong Firestore để nhân viên không đăng nhập được vào app).*

---

## 3. Fragment: Thống kê doanh thu (RevenueStatisticsFragment)

### A. Giao diện (UI)
- **Bộ lọc:** `Spinner` hoặc `RadioGroup` để chọn chế độ: "Theo Ngày" hoặc "Theo Tháng".
- **Biểu đồ:** Sử dụng thư viện `MPAndroidChart` để vẽ `BarChart` (Biểu đồ cột).
- **Tổng kết:** Một `TextView` lớn hiển thị tổng số tiền của khoảng thời gian đã chọn.

### B. Logic xử lý
- **Truy vấn dữ liệu:**
    - Query collection `invoices`.
    - Dùng `whereGreaterThanOrEqualTo` và `whereLessThanOrEqualTo` dựa trên Timestamp để lọc dữ liệu theo ngày/tháng khách chọn.
- **Xử lý biểu đồ:**
    - Duyệt danh sách hóa đơn trả về, cộng dồn doanh thu vào một `HashMap<String, Double>` (với Key là Ngày hoặc Tháng).
    - Chuyển HashMap thành danh sách `BarEntry` để đẩy vào Biểu đồ.
- **Định dạng:** Sử dụng `DecimalFormat` để hiển thị tiền VND (ví dụ: 1.500.000đ).

---

## 4. Cấu trúc thư mục khuyến nghị
- `com.example.billards.fragments.admin`
    - `StaffManagementFragment.java`
    - `RevenueStatisticsFragment.java`
- `com.example.billards.adapters`
    - `StaffAdapter.java`
- `layout`
    - `fragment_staff_management.xml`
    - `fragment_revenue_statistics.xml`
    - `item_staff.xml` (Giao diện cho 1 dòng nhân viên)

---

## 5. Task Breakdown (Ngày 12 - 14)
- **Buổi 1 (3h):** Code xong giao diện Fragment Quản lý nhân viên và logic Hiển thị/Thêm.
- **Buổi 2 (3h):** Code chức năng Xóa và cài đặt thư viện Biểu đồ.
- **Buổi 3 (3h):** Viết logic lọc dữ liệu Firestore và vẽ biểu đồ doanh thu.