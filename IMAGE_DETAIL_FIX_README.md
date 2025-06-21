# Sửa lỗi xử lý ảnh trong màn hình chi tiết

## Các vấn đề đã phát hiện và sửa

### 1. **URL ảnh không đúng**
**Vấn đề**: BASE_URL hardcode là `http://192.168.1.4:8000` nhưng server thực tế là `http://192.168.1.3:8000`

**Giải pháp**: 
```java
// Trước đây
String BASE_URL = "http://192.168.1.4:8000";

// Bây giờ  
String BASE_URL = "http://192.168.1.3:8000"; // Sử dụng cùng URL với RetrofitClient
```

### 2. **Xử lý URL ảnh không nhất quán**
**Vấn đề**: Chỉ xử lý URL bắt đầu bằng `/storage`, bỏ qua các URL khác

**Giải pháp**:
```java
// Trước đây
if (url != null && url.startsWith("/storage")) {
    url = BASE_URL + url;
}

// Bây giờ
if (url != null) {
    if (url.startsWith("/storage") || url.startsWith("/")) {
        url = BASE_URL + url;
    } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
        url = BASE_URL + "/" + url;
    }
    imageUrls.add(url);
}
```

### 3. **Thiếu xử lý lỗi khi load ảnh**
**Vấn đề**: Không có logging và xử lý lỗi khi ảnh không load được

**Giải pháp**: Thêm RequestListener cho Glide
```java
Glide.with(context)
    .load(url)
    .placeholder(R.drawable.bg_avatar_placeholder)
    .error(R.drawable.bg_avatar_placeholder)
    .centerCrop()
    .listener(new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            Log.e(TAG, "Failed to load image: " + url, e);
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            Log.d(TAG, "Successfully loaded image: " + url);
            return false;
        }
    })
    .into(holder.imageView);
```

### 4. **Thiếu kiểm tra null**
**Vấn đề**: Có thể gây crash khi `imageUrls` null

**Giải pháp**:
```java
// Trước đây
return imageUrls.size();

// Bây giờ
return imageUrls != null ? imageUrls.size() : 0;
```

### 5. **Xử lý URL avatar user**
**Vấn đề**: URL avatar user không được xử lý đúng cách

**Giải pháp**:
```java
// Xử lý URL avatar user
if (userAvatarUrl != null && !userAvatarUrl.isEmpty()) {
    if (userAvatarUrl.startsWith("/storage") || userAvatarUrl.startsWith("/")) {
        userAvatarUrl = BASE_URL + userAvatarUrl;
    } else if (!userAvatarUrl.startsWith("http://") && !userAvatarUrl.startsWith("https://")) {
        userAvatarUrl = BASE_URL + "/" + userAvatarUrl;
    }
}
```

### 6. **Cải thiện hiển thị ViewPager**
**Vấn đề**: ViewPager luôn hiển thị ngay cả khi không có ảnh

**Giải pháp**:
```java
if (data.imageUrls != null && !data.imageUrls.isEmpty()) {
    imagePager.setAdapter(new ImagePagerAdapter(requireContext(), data.imageUrls));
    imagePager.setVisibility(View.VISIBLE);
} else {
    imagePager.setVisibility(View.GONE);
}
```

## Các file đã được sửa

1. **DetailViewModel.java**: Sửa xử lý URL ảnh và avatar
2. **ImagePagerAdapter.java**: Thêm logging và xử lý lỗi
3. **DetailFragment.java**: Cải thiện hiển thị và thêm logging

## Kết quả

✅ **URL ảnh đúng**: Ảnh sẽ load được từ server đúng  
✅ **Xử lý lỗi tốt hơn**: Có logging và fallback khi ảnh không load được  
✅ **Không crash**: Kiểm tra null đầy đủ  
✅ **Hiển thị thông minh**: Ẩn ViewPager khi không có ảnh  
✅ **Debug dễ dàng**: Có logging chi tiết để debug  

## Cách debug

Khi gặp vấn đề với ảnh, kiểm tra log với tag:
- `DetailFragment`: Thông tin về dữ liệu nhận được
- `ImagePagerAdapter`: Thông tin về việc load ảnh

Ví dụ:
```
DetailFragment: Received detail data: Laptop Gaming, images: 2
DetailFragment: Loading user avatar: http://192.168.1.3:8000/storage/avatars/user1.jpg
ImagePagerAdapter: Loading image at position 0: http://192.168.1.3:8000/storage/item-images/laptop1.jpg
ImagePagerAdapter: Successfully loaded image: http://192.168.1.3:8000/storage/item-images/laptop1.jpg
``` 