# Sửa style Spinner thành nền trắng

## Vấn đề
Combobox (Spinner) ở phần chọn danh mục đồ vật trong cả đăng tin và sửa tin đang sử dụng Material 3 style mặc định, cần thay đổi thành nền trắng để đồng bộ với thiết kế tổng thể.

## Giải pháp

### 1. Tạo custom layout cho Spinner

#### **spinner_item_white.xml** - Layout cho item được chọn
```xml
<?xml version="1.0" encoding="utf-8"?>
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@android:id/text1"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@color/white"
    android:padding="16dp"
    android:textSize="16sp"
    android:textColor="@color/text_primary"
    android:singleLine="true"
    android:ellipsize="end" />
```

#### **spinner_dropdown_item_white.xml** - Layout cho dropdown items
```xml
<?xml version="1.0" encoding="utf-8"?>
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@android:id/text1"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@color/white"
    android:padding="16dp"
    android:textSize="16sp"
    android:textColor="@color/text_primary"
    android:singleLine="true"
    android:ellipsize="end" />
```

### 2. Cập nhật code trong Fragment

#### Trước đây (Material 3 style):
```java
ArrayAdapter<String> adapter = new ArrayAdapter<String>(
    requireContext(), 
    android.R.layout.simple_spinner_item, 
    categories
);
adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
```

#### Bây giờ (Custom white background):
```java
ArrayAdapter<String> adapter = new ArrayAdapter<String>(
    requireContext(), 
    R.layout.spinner_item_white, 
    categories
);
adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_white);
```

## Các file đã được cập nhật

### Layout files (mới):
1. **spinner_item_white.xml**: Layout cho spinner item được chọn
2. **spinner_dropdown_item_white.xml**: Layout cho dropdown items

### Java files:
1. **CreatePostFragment.java**: Cập nhật `setupCategorySpinner()`
2. **EditPostFragment.java**: Cập nhật `setupCategorySpinner()`

## Chi tiết thay đổi

### CreatePostFragment.java
```java
// Trước đây
ArrayAdapter<String> adapter = new ArrayAdapter<String>(
    requireContext(), 
    android.R.layout.simple_spinner_item, 
    categories
);
adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Bây giờ
ArrayAdapter<String> adapter = new ArrayAdapter<String>(
    requireContext(), 
    R.layout.spinner_item_white, 
    categories
);
adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_white);
```

### EditPostFragment.java
```java
// Trước đây
ArrayAdapter<String> adapter = new ArrayAdapter<>(
    requireContext(), 
    android.R.layout.simple_spinner_item, 
    categories
);
adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Bây giờ
ArrayAdapter<String> adapter = new ArrayAdapter<>(
    requireContext(), 
    R.layout.spinner_item_white, 
    categories
);
adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_white);
```

## Kết quả

✅ **Nền trắng**: Spinner có nền trắng thay vì Material 3 style  
✅ **Đồng bộ thiết kế**: Phù hợp với theme tổng thể của ứng dụng  
✅ **Nhất quán**: Cả đăng tin và sửa tin đều có cùng style  
✅ **Dễ đọc**: Text màu đen trên nền trắng dễ đọc hơn  

## Tính năng của custom layout

- **Nền trắng**: `android:background="@color/white"`
- **Padding phù hợp**: `android:padding="16dp"`
- **Text size chuẩn**: `android:textSize="16sp"`
- **Text color**: `android:textColor="@color/text_primary"`
- **Single line**: `android:singleLine="true"`
- **Ellipsize**: `android:ellipsize="end"` cho text dài

## Lưu ý

- Custom layout vẫn giữ nguyên logic xử lý cho item đầu tiên ("Chọn danh mục")
- Text color sẽ được thay đổi động trong code: màu xám cho placeholder, màu đen cho các option thực
- Layout có thể được tái sử dụng cho các spinner khác trong ứng dụng 