# Thiết Kế Lại Layout Landscape Màn Hình Đăng Nhập

## Tổng Quan
Đã thiết kế lại layout landscape cho màn hình đăng nhập dựa trên mẫu HTML hiện đại với thiết kế 2 cột: cột trái có hình ảnh nền và text, cột phải có form đăng nhập. Layout mới có hiệu ứng động khi cuộn và đồng bộ giữa 2 cột.

## Các Thay Đổi Chính

### 1. Layout Landscape Mới (`layout-land/activity_login.xml`)
- **Thiết kế 2 cột**: 50% mỗi cột với LinearLayout
- **Cột trái (leftScrollView)**:
  - Background gradient xanh TLU phủ toàn bộ
  - Text "Cùng TLU, tìm lại những gì đã mất." và mô tả
  - Có thể cuộn và đồng bộ với cột phải
- **Cột phải (rightScrollView)**:
  - Form đăng nhập với thiết kế hiện đại
  - Có thể cuộn và đồng bộ với cột trái
  - Logo và tên app "Tìm đồ thất lạc TLU"

### 2. Hiệu Ứng Động
- **Đồng bộ cuộn**: Khi cuộn form bên phải, phần quote bên trái cũng di chuyển theo
- **ScrollView độc lập**: Mỗi cột có ScrollView riêng để tối ưu hiệu suất
- **Đồng bộ 2 chiều**: Cuộn từ trái hoặc phải đều đồng bộ với nhau

### 3. Drawable Mới
- **`login_background_gradient.xml`**: Gradient xanh TLU cho background cột trái
- **`login_input_background.xml`**: Background cho input fields với border và corner radius

### 4. Cập Nhật Màu Sắc
- **`colors.xml`**: Thay đổi `primary_blue` từ `#0055A4` thành `#0D47A1` để phù hợp với thiết kế mới

### 5. Cập Nhật LoginFragment
- Thêm xử lý sự kiện click cho `forgotPasswordTextView`
- Thêm logic đồng bộ cuộn giữa 2 ScrollView
- Hiển thị toast thông báo "Tính năng quên mật khẩu đang được phát triển!"

### 6. Cập Nhật Các Layout Khác
- **Layout chính**: Thêm `forgotPasswordTextView` vào cạnh checkbox
- **Layout w936dp**: Cập nhật cấu trúc tương tự
- **Layout w1240dp**: Cập nhật cấu trúc tương tự

## Chi Tiết Kỹ Thuật

### Cấu Trúc Layout Landscape
```xml
<LinearLayout android:orientation="horizontal">
  <!-- Cột trái - Có thể cuộn -->
  <ScrollView android:id="@+id/leftScrollView">
    <ConstraintLayout android:id="@+id/imagePanel">
      <LinearLayout android:id="@+id/quoteContainer">
        <TextView android:id="@+id/quoteTitle" />
        <TextView android:id="@+id/quoteDescription" />
      </LinearLayout>
    </ConstraintLayout>
  </ScrollView>
  
  <!-- Cột phải - Có thể cuộn -->
  <ScrollView android:id="@+id/rightScrollView">
    <ConstraintLayout android:id="@+id/formPanel">
      <LinearLayout android:id="@+id/formContainer">
        <!-- Logo và tên app -->
        <LinearLayout android:id="@+id/logoContainer">
          <ImageView android:id="@+id/logoImageView" />
          <TextView android:id="@+id/appNameTextView" />
        </LinearLayout>
        
        <!-- Form fields -->
        <TextView android:id="@+id/titleTextView" />
        <TextView android:id="@+id/subtitleTextView" />
        <!-- Email và Password inputs -->
        <!-- Remember và Forgot Password -->
        <!-- Login Button -->
        <!-- Sign up text -->
      </LinearLayout>
    </ConstraintLayout>
  </ScrollView>
</LinearLayout>
```

### Logic Đồng Bộ Cuộn
```java
private void setupScrollSync() {
    // Đồng bộ cuộn từ phải sang trái
    rightScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
        if (rightScrollView.getScrollY() != leftScrollView.getScrollY()) {
            leftScrollView.scrollTo(0, rightScrollView.getScrollY());
        }
    });

    // Đồng bộ cuộn từ trái sang phải
    leftScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
        if (leftScrollView.getScrollY() != rightScrollView.getScrollY()) {
            rightScrollView.scrollTo(0, leftScrollView.getScrollY());
        }
    });
}
```

### Gradient Background
```xml
<gradient
    android:angle="135"
    android:startColor="#0D47A1"
    android:centerColor="#1976D2"
    android:endColor="#42A5F5"
    android:type="linear" />
```

### Input Background
```xml
<shape>
    <solid android:color="#FFFFFF" />
    <stroke android:width="1dp" android:color="#E0E0E0" />
    <corners android:radius="8dp" />
</shape>
```

## Responsive Design
- **Landscape**: Thiết kế 2 cột với hình ảnh và form, có hiệu ứng động khi cuộn
- **Portrait**: Giữ nguyên thiết kế dọc với logo ở trên, form ở dưới
- **Màn hình lớn**: Tối ưu spacing và kích thước cho tablet

## Tính Năng Mới
1. **Quên mật khẩu**: Link clickable với thông báo placeholder
2. **Thiết kế hiện đại**: Gradient background, input fields có border, spacing tốt hơn
3. **Branding TLU**: Màu sắc và text phù hợp với thương hiệu TLU
4. **Hiệu ứng động**: Phần quote di chuyển theo khi cuộn form
5. **Đồng bộ cuộn**: 2 cột cuộn đồng bộ với nhau

## Kết Quả
- Layout landscape đẹp mắt và chuyên nghiệp
- Responsive hoàn hảo trên mọi kích thước màn hình
- Trải nghiệm người dùng tốt hơn với thiết kế hiện đại và hiệu ứng động
- Tính nhất quán giữa các layout khác nhau
- Hiệu ứng cuộn mượt mà và đồng bộ 