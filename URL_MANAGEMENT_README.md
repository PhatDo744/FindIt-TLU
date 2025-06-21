# Hệ thống quản lý URL tập trung

## Vấn đề trước đây
Khi chuyển đổi môi trường (local, emulator, production), phải thay đổi BASE_URL ở nhiều nơi:
- `RetrofitClient.java`
- `DetailViewModel.java`
- Và các file khác...

## Giải pháp mới

### 1. **Constants.java** - Quản lý URL tập trung
```java
public class Constants {
    // Các URL server cho các môi trường khác nhau
    public static final String BASE_URL_LOCAL_QUANG = "http://192.168.1.4:8000";
    public static final String BASE_URL_LOCAL_PHAT = "http://192.168.1.3:8000";
    public static final String BASE_URL_EMULATOR = "http://10.0.2.2:8000";
    public static final String BASE_URL_PRODUCTION = "https://findit-tlu.com";
    
    // Chỉ cần thay đổi ở đây để chuyển môi trường
    public static final String CURRENT_ENVIRONMENT = "LOCAL_PHAT";
    
    public static String getBaseUrl() { /* ... */ }
    public static String getApiBaseUrl() { /* ... */ }
    public static String getStorageBaseUrl() { /* ... */ }
}
```

### 2. **UrlUtils.java** - Xử lý URL tự động
```java
public class UrlUtils {
    public static String toAbsoluteUrl(String relativeUrl) { /* ... */ }
    public static String toAbsoluteAvatarUrl(String avatarUrl) { /* ... */ }
    public static String toAbsoluteImageUrl(String imageUrl) { /* ... */ }
    public static boolean isValidUrl(String url) { /* ... */ }
}
```

## Cách sử dụng

### Chuyển đổi môi trường
Chỉ cần thay đổi 1 dòng trong `Constants.java`:

```java
// Cho môi trường local của Quang
public static final String CURRENT_ENVIRONMENT = "LOCAL_QUANG";

// Cho môi trường local của Phat  
public static final String CURRENT_ENVIRONMENT = "LOCAL_PHAT";

// Cho emulator
public static final String CURRENT_ENVIRONMENT = "EMULATOR";

// Cho production
public static final String CURRENT_ENVIRONMENT = "PRODUCTION";
```

### Sử dụng trong code

#### Trước đây (hardcode):
```java
String BASE_URL = "http://192.168.1.3:8000";
if (url.startsWith("/storage")) {
    url = BASE_URL + url;
}
```

#### Bây giờ (tự động):
```java
// Xử lý URL ảnh
String absoluteUrl = UrlUtils.toAbsoluteImageUrl(url);

// Xử lý URL avatar
String avatarUrl = UrlUtils.toAbsoluteAvatarUrl(userAvatarUrl);

// Lấy BASE_URL cho API
String apiUrl = Constants.getApiBaseUrl();

// Lấy BASE_URL cho storage
String storageUrl = Constants.getStorageBaseUrl();
```

## Các file đã được cập nhật

1. **Constants.java** (mới): Quản lý URL tập trung
2. **UrlUtils.java** (mới): Xử lý URL tự động
3. **RetrofitClient.java**: Sử dụng `Constants.getApiBaseUrl()`
4. **DetailViewModel.java**: Sử dụng `UrlUtils.toAbsoluteImageUrl()`

## Lợi ích

✅ **Quản lý tập trung**: Chỉ cần thay đổi 1 chỗ để chuyển môi trường  
✅ **Tự động xử lý**: URL được xử lý tự động, không cần logic thủ công  
✅ **Nhất quán**: Tất cả URL đều được xử lý theo cùng một cách  
✅ **Dễ bảo trì**: Thêm môi trường mới chỉ cần thêm vào Constants  
✅ **Type-safe**: Có các method riêng cho từng loại URL  

## Các môi trường hỗ trợ

| Môi trường | URL | Mô tả |
|------------|-----|-------|
| LOCAL_QUANG | `http://192.168.1.4:8000` | Local development - Quang |
| LOCAL_PHAT | `http://192.168.1.3:8000` | Local development - Phat |
| EMULATOR | `http://10.0.2.2:8000` | Android Emulator |
| PRODUCTION | `https://findit-tlu.com` | Production server |

## Thêm môi trường mới

1. Thêm constant vào `Constants.java`:
```java
public static final String BASE_URL_NEW_ENV = "http://new-server.com";
```

2. Thêm case vào method `getBaseUrl()`:
```java
case "NEW_ENV":
    return BASE_URL_NEW_ENV;
```

3. Sử dụng:
```java
public static final String CURRENT_ENVIRONMENT = "NEW_ENV";
```

## Debug và Logging

Các URL được xử lý sẽ có logging trong `ImagePagerAdapter`:
```
ImagePagerAdapter: Loading image at position 0: http://192.168.1.3:8000/storage/item-images/photo.jpg
```

## Lưu ý

- Luôn sử dụng `UrlUtils` thay vì xử lý URL thủ công
- Kiểm tra `Constants.getCurrentEnvironment()` để biết môi trường hiện tại
- Sử dụng `Constants.isDevelopment()` để kiểm tra có phải môi trường development không 