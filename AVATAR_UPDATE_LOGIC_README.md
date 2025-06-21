# Cải thiện Logic Upload Ảnh Đại Diện

## Vấn đề trước đây
Trong trang thay đổi thông tin cá nhân (`EditProfileFragment`), khi người dùng chọn ảnh mới, ảnh sẽ được upload ngay lập tức lên server mà không cần đợi người dùng ấn nút "Lưu". Điều này gây ra:

1. **Upload không cần thiết**: Nếu người dùng chọn ảnh nhưng sau đó ấn "Hủy", ảnh vẫn đã được upload
2. **Trải nghiệm người dùng kém**: Không có sự kiểm soát về việc khi nào thực sự lưu thay đổi
3. **Lãng phí tài nguyên**: Upload ảnh ngay cả khi không cần thiết

## Giải pháp mới

### Logic mới:
1. **Khi chọn ảnh**: Chỉ hiển thị preview, không upload
2. **Khi ấn "Lưu"**: Upload ảnh (nếu có) và cập nhật thông tin
3. **Khi ấn "Hủy"**: Khôi phục lại ảnh cũ, không upload

### Các thay đổi chính:

#### 1. Thêm các biến theo dõi trạng thái:
```java
private boolean hasAvatarChanged = false; // Flag để kiểm tra ảnh có thay đổi không
private String originalAvatarUrl; // Lưu URL ảnh gốc
```

#### 2. Thay đổi logic chọn ảnh:
```java
// Trước đây: Upload ngay khi chọn
userViewModel.uploadAvatar(requireContext(), uri).observe(...);

// Bây giờ: Chỉ preview
selectedAvatarUri = uri;
hasAvatarChanged = true;
avatarImageView.setImageURI(uri);
CustomToast.showCustomToast(getContext(), "Thông báo", "Ảnh đã được chọn. Nhấn 'Lưu' để cập nhật!");
```

#### 3. Cải thiện logic nút "Lưu":
```java
// Kiểm tra và upload ảnh trước nếu cần
if (hasAvatarChanged && selectedAvatarUri != null) {
    userViewModel.uploadAvatar(requireContext(), selectedAvatarUri).observe(...);
} else {
    updateUserProfile(fullName, phone, email, navController, saveButton);
}
```

#### 4. Cải thiện logic nút "Hủy":
```java
// Khôi phục lại ảnh cũ nếu đã thay đổi
if (hasAvatarChanged && originalAvatarUrl != null) {
    ImageUtils.loadAvatar(requireContext(), originalAvatarUrl, avatarImageView);
}
```

### Lợi ích:

1. **Kiểm soát tốt hơn**: Người dùng có thể xem preview trước khi quyết định lưu
2. **Tiết kiệm tài nguyên**: Chỉ upload khi thực sự cần thiết
3. **Trải nghiệm người dùng tốt hơn**: Có thể hủy thay đổi mà không ảnh hưởng đến server
4. **Validation tốt hơn**: Kiểm tra dữ liệu đầu vào trước khi lưu

### Luồng hoạt động mới:

1. **Mở trang chỉnh sửa**: Load thông tin hiện tại
2. **Chọn ảnh mới**: Hiển thị preview, đánh dấu `hasAvatarChanged = true`
3. **Ấn "Lưu"**: 
   - Nếu có ảnh mới → Upload ảnh trước → Cập nhật thông tin
   - Nếu không có ảnh mới → Chỉ cập nhật thông tin
4. **Ấn "Hủy"**: Khôi phục lại ảnh cũ, không thay đổi gì

### Validation bổ sung:
- Kiểm tra họ tên không được để trống
- Kiểm tra số điện thoại không được để trống
- Disable nút "Lưu" trong quá trình xử lý để tránh click nhiều lần 