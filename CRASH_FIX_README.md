# Khắc phục lỗi Crash Snackbar trong Fragment

## Vấn đề
Ứng dụng bị crash với lỗi:
```
java.lang.IllegalArgumentException: No suitable parent found from the given view. Please provide a valid view.
at com.google.android.material.snackbar.Snackbar.makeInternal(Snackbar.java:205)
```

## Nguyên nhân
Lỗi xảy ra khi Fragment chưa được attach vào Activity hoặc view chưa được tạo, việc gọi `Snackbar.make(view, ...)` sẽ gây ra lỗi "No suitable parent found from the given view".

## Các file đã được sửa

### 1. PostListByTypeFragment.java
- **Vấn đề**: Sử dụng `getView()` làm parent cho Snackbar
- **Giải pháp**: Thay thế bằng `getActivity().findViewById(android.R.id.content)` và thêm kiểm tra `isAdded()`

### 2. EditPostFragment.java
- **Vấn đề**: Sử dụng `requireView()` làm parent cho Snackbar
- **Giải pháp**: Thay thế bằng `getActivity().findViewById(android.R.id.content)` và thêm kiểm tra `isAdded()`

### 3. CreatePostFragment.java
- **Vấn đề**: Sử dụng `requireView()` làm parent cho Snackbar
- **Giải pháp**: Thay thế bằng `getActivity().findViewById(android.R.id.content)` và thêm kiểm tra `isAdded()`

## Pattern sửa lỗi

### Trước khi sửa:
```java
Snackbar.make(requireView(), "Thông báo", Snackbar.LENGTH_SHORT).show();
```

### Sau khi sửa:
```java
if (isAdded() && getActivity() != null) {
    Snackbar.make(getActivity().findViewById(android.R.id.content), "Thông báo", Snackbar.LENGTH_SHORT).show();
} else {
    android.widget.Toast.makeText(getContext(), "Thông báo", android.widget.Toast.LENGTH_SHORT).show();
}
```

## Utility class mới
Đã tạo `SafeSnackbar.java` để xử lý Snackbar một cách an toàn:

```java
// Sử dụng đơn giản
SafeSnackbar.showShort(this, "Thông báo ngắn");
SafeSnackbar.showLong(this, "Thông báo dài");
```

## Lưu ý
- Luôn kiểm tra `isAdded()` trước khi sử dụng Fragment
- Sử dụng `getActivity().findViewById(android.R.id.content)` thay vì `getView()` hoặc `requireView()`
- Có fallback về Toast nếu Fragment chưa sẵn sàng
- Kiểm tra `getActivity() != null` để tránh NPE

## Kết quả
- Ứng dụng không còn bị crash khi hiển thị Snackbar
- Cải thiện trải nghiệm người dùng
- Code an toàn hơn với các kiểm tra null và trạng thái Fragment 