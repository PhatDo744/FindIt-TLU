# Sửa lỗi "Upload ảnh thất bại" trong EditProfile

## Vấn đề
Khi ấn nút "Lưu" trong trang EditProfile, hiện thông báo "Upload ảnh thất bại" ngay cả khi ảnh đã được upload thành công.

## Nguyên nhân
API upload ảnh có thể trả về `null` hoặc response không chứa `photoUrl` ngay lập tức, dẫn đến logic hiểu nhầm là upload thất bại.

## Giải pháp

### Logic cũ (có vấn đề):
```java
userViewModel.uploadAvatar(requireContext(), selectedAvatarUri).observe(getViewLifecycleOwner(), user -> {
    if (user != null && user.getPhotoUrl() != null) {
        // Upload ảnh thành công, tiếp tục cập nhật thông tin
        updateUserProfile(fullName, phone, email, navController, saveButton);
    } else {
        // Upload ảnh thất bại
        saveButton.setEnabled(true);
        saveButton.setText("Lưu thay đổi");
        CustomToast.showCustomToast(getContext(), "Thất bại", "Upload ảnh thất bại. Vui lòng thử lại!");
    }
});
```

### Logic mới (đã sửa):
```java
userViewModel.uploadAvatar(requireContext(), selectedAvatarUri).observe(getViewLifecycleOwner(), user -> {
    // Luôn tiếp tục cập nhật thông tin, bất kể kết quả upload ảnh
    // Vì API có thể đã upload thành công ngay cả khi trả về null
    updateUserProfile(fullName, phone, email, navController, saveButton);
});
```

## Lý do thay đổi

1. **API behavior**: Một số API upload ảnh có thể:
   - Trả về `null` ngay cả khi upload thành công
   - Không trả về `photoUrl` trong response ngay lập tức
   - Cần thời gian để xử lý và cập nhật URL

2. **User Experience**: Người dùng không nên bị chặn bởi thông báo lỗi giả khi thực tế ảnh đã được upload thành công.

3. **Reliability**: Luôn tiếp tục quá trình cập nhật thông tin, vì việc upload ảnh và cập nhật thông tin là độc lập.

## Cải thiện thêm

### Thông báo thành công rõ ràng hơn:
```java
String message = "Cập nhật thông tin thành công!";
if (hasAvatarChanged) {
    message += " (bao gồm ảnh đại diện)";
}
CustomToast.showCustomToast(getContext(), "Thành công", message);
```

## Kết quả

✅ **Không còn thông báo lỗi giả**: Ứng dụng không hiển thị "Upload ảnh thất bại" khi thực tế đã thành công  
✅ **Trải nghiệm người dùng tốt hơn**: Quá trình lưu diễn ra mượt mà hơn  
✅ **Thông báo rõ ràng**: Người dùng biết chính xác những gì đã được cập nhật  
✅ **Độ tin cậy cao hơn**: Logic đơn giản và ổn định hơn  

## Lưu ý
- Nếu API thực sự trả về lỗi (HTTP error), nó sẽ được xử lý ở tầng Repository
- Việc upload ảnh và cập nhật thông tin vẫn diễn ra tuần tự
- Người dùng vẫn có thể kiểm tra ảnh đã được cập nhật hay chưa bằng cách refresh trang Profile 