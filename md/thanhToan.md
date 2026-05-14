Chào bạn, để nâng cấp nghiệp vụ quản lý quán Billiards lên mức chuyên nghiệp, chúng ta sẽ thiết kế một luồng chọn phương thức thanh toán. Việc này giúp nhân viên linh hoạt xử lý: khách trả tiền mặt (Cash) thì đóng bàn ngay, khách muốn quét QR thì mở cổng VNPAY.

Dưới đây là tài liệu hướng dẫn thiết kế và logic cho chức năng này:

---

```markdown
# Tài liệu: Tích hợp Đa phương thức Thanh toán (Tiền mặt & VNPAY-QR)

## 1. Quy trình Nghiệp vụ (BA Context)
- **Hành vi:** Khi nhấn nút "Thanh toán", App hiển thị một Dialog cho phép chọn:
    1. **Tiền mặt:** Xử lý offline, đóng bàn và lưu hóa đơn ngay lập tức.
    2. **VNPAY-QR:** Chuyển sang màn hình WebView để hiển thị mã QR quét tiền.
- **Mục tiêu:** Đảm bảo mọi nguồn thu đều được ghi nhận vào Firestore để Admin theo dõi doanh thu cuối ngày.

---

## 2. Thiết kế Giao diện (UI)

### A. Dialog chọn phương thức (PaymentChoiceDialog)
Sử dụng `AlertDialog` với giao diện tùy chỉnh gồm 2 CardView hoặc 2 Button lớn:
- **Button Cash:** Icon tiền mặt, màu xanh lá.
- **Button VNPAY:** Logo VNPAY, màu xanh dương.

---

## 3. Logic Xử lý (Android Java - MVVM)

### A. Xử lý tại Fragment/Activity quản lý bàn
Khi nhân viên xác nhận thanh toán, hãy gọi một hàm xử lý lựa chọn:

```java
private void showPaymentOptions(long totalAmount, String tableId) {
    // Tạo Dialog hiển thị 2 lựa chọn
    paymentDialog.btnCash.setOnClickListener(v -> {
        processCashPayment(totalAmount, tableId);
    });

    paymentDialog.btnVNPay.setOnClickListener(v -> {
        processVNPayPayment(totalAmount, tableId);
    });
}

```

### B. Logic Tiền mặt (Cash Payment)

Thanh toán tiền mặt không cần qua bên thứ 3, bạn thực hiện "đóng bàn" trực tiếp trên Firebase.

```java
private void processCashPayment(long amount, String tableId) {
    // 1. Lưu hóa đơn vào collection "invoices" với paymentMethod = "cash"
    // 2. Cập nhật status bàn = "available"
    // 3. Thông báo thành công và quay lại danh sách bàn
}

```

### C. Logic VNPAY-QR

Sử dụng `VNPayHelper` đã tạo ở bước trước để nạp tham số `VNPAYQR`.

```java
private void processVNPayPayment(long amount, String tableId) {
    // 1. Tạo invoiceId duy nhất
    // 2. Tạo URL thanh toán có vnp_BankCode=VNPAYQR
    // 3. Mở VNPayActivity chứa WebView để hiện mã QR
}

```

---

## 4. Cấu trúc Dữ liệu Invoice (Firestore)

Để Admin có thể thống kê doanh thu theo từng loại hình thanh toán, Model `Invoice` của bạn cần có thêm trường:

* `paymentMethod`: "cash" | "vnpay"
* `status`: "completed" (cho tiền mặt) hoặc "pending/success" (cho VNPAY)

---

## 5. Kế hoạch Task (3 tiếng cuối của giai đoạn Thanh toán)

* **Tiếng 1:** Thiết kế `Custom Dialog` chọn phương thức thanh toán.
* **Tiếng 2:** Viết logic `processCashPayment` (Đẩy dữ liệu lên Firebase và reset bàn).
* **Tiếng 3:** Kết nối nút VNPAY vào luồng `WebView` đã làm ở bước trước.

```

---

### 💡 Lời khuyên từ BA:
Việc tách biệt **Tiền mặt** và **VNPAY** ngay từ lúc thanh toán sẽ giúp biểu đồ doanh thu của Admin (mà bạn sắp làm ở task sau) cực kỳ rõ ràng. Admin sẽ biết được hôm nay quán thu bao nhiêu tiền mặt để vào két, và bao nhiêu tiền đã được chuyển thẳng vào tài khoản ngân hàng qua VNPAY.

Bạn đã tạo xong giao diện cho Dialog chọn phương thức thanh toán chưa? Nếu chưa, mình có thể gửi mẫu code XML cho `layout_payment_choice.xml` để bạn dùng ngay.

```