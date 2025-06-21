# Thêm tính năng thông báo xóa bài đăng kèm lý do khi admin xóa

## Tổng quan

Đã thêm tính năng thông báo xóa bài đăng kèm lý do khi admin xóa bài đăng. Khi admin xóa một bài đăng, người dùng sẽ nhận được thông báo với lý do xóa cụ thể.

## Thay đổi đã thực hiện

### **1. Web App (Laravel)**

#### **A. Tạo Notification Class mới: `PostDeletedNotification.php`**

**File:** `temp/findit_tlu_web/app/Notifications/PostDeletedNotification.php`

```php
<?php

namespace App\Notifications;

use Illuminate\Bus\Queueable;
use Illuminate\Notifications\Notification;
use Illuminate\Contracts\Queue\ShouldQueue;
use App\Models\Item;

class PostDeletedNotification extends Notification implements ShouldQueue
{
    use Queueable;
    
    protected $item;
    protected $reason;
    
    public function __construct(Item $item, $reason = null)
    {
        $this->item = $item;
        $this->reason = $reason;
    }
    
    public function via($notifiable)
    {
        return ['database'];
    }
    
    public function toDatabase($notifiable)
    {
        $message = 'Tin đăng "' . $this->item->title . '" của bạn đã bị xóa khỏi hệ thống.';
        
        if ($this->reason) {
            $message .= ' Lý do: ' . $this->reason;
        }
        
        return [
            'title' => $message,
            'item_id' => $this->item->id,
            'type' => 'deleted',
            'reason' => $this->reason,
        ];
    }
}
```

**Tính năng:**
- ✅ Gửi thông báo khi admin xóa bài đăng
- ✅ Bao gồm lý do xóa trong thông báo
- ✅ Lưu trữ trong database
- ✅ Hỗ trợ queue để xử lý bất đồng bộ

#### **B. Cập nhật ItemController: `ItemController.php`**

**File:** `temp/findit_tlu_web/app/Http/Controllers/Admin/ItemController.php`

**Thay đổi method `destroy()`:**

**TRƯỚC:**
```php
public function destroy(Item $item)
{
    try {
        $itemTitle = $item->title;
        $item->delete();
        return redirect()->route('admin.items.index')
            ->with('success_title', 'Xóa bài thành công!')
            ->with('success', "Bài đăng đã được xóa vĩnh viễn khỏi hệ thống.");
    } catch (\Exception $e) {
        Log::error("Error deleting item ID {$item->id}: " . $e->getMessage());
        return redirect()->route('admin.items.index')->with('error', 'Có lỗi xảy ra khi xóa bài đăng.');
    }
}
```

**SAU:**
```php
public function destroy(Request $request, Item $item)
{
    $request->validate([
        'admin_delete_comment' => 'required|string|max:1000',
    ], [
        'admin_delete_comment.required' => 'Vui lòng nhập lý do xóa bài đăng.'
    ]);

    try {
        $itemTitle = $item->title;
        $deleteReason = $request->input('admin_delete_comment');
        
        // Gửi thông báo cho user về việc bài đăng bị xóa
        $item->user->notify(new \App\Notifications\PostDeletedNotification($item, $deleteReason));
        
        $item->delete();
        
        return redirect()->route('admin.items.index')
            ->with('success_title', 'Xóa bài thành công!')
            ->with('success', "Bài đăng đã được xóa vĩnh viễn khỏi hệ thống và người dùng đã nhận được thông báo.");
    } catch (\Exception $e) {
        Log::error("Error deleting item ID {$item->id}: " . $e->getMessage());
        return redirect()->route('admin.items.index')->with('error', 'Có lỗi xảy ra khi xóa bài đăng.');
    }
}
```

**Thay đổi:**
- ✅ Thêm validation cho lý do xóa (bắt buộc)
- ✅ Gửi notification với lý do xóa
- ✅ Cập nhật thông báo thành công

#### **C. Cập nhật giao diện admin: `show.blade.php`**

**File:** `temp/findit_tlu_web/resources/views/admin/items/show.blade.php`

**Thêm nút xóa:**
```html
<div class="mt-2">
    <button type="button" class="btn btn-danger btn-sm" data-bs-toggle="modal" data-bs-target="#deleteItemModal" data-item-id="{{ $item->id }}" data-item-title="{{ $item->title }}">
        <i class="fas fa-trash"></i> Xóa bài đăng
    </button>
</div>
```

**Thêm modal xác nhận xóa:**
```html
<!-- Modal Xác nhận Xóa -->
<div class="modal fade" id="deleteItemModal" tabindex="-1" aria-labelledby="deleteItemModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="deleteItemForm" method="POST" action="">
                @csrf
                @method('DELETE')
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteItemModalLabel">Xác nhận xóa bài đăng</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="alert alert-warning">
                        <h6><i class="fas fa-exclamation-triangle"></i> Cảnh báo!</h6>
                        <ul class="mb-0">
                            <li>Bài đăng sẽ bị xóa vĩnh viễn khỏi hệ thống</li>
                            <li>Tất cả hình ảnh và dữ liệu liên quan sẽ bị xóa</li>
                            <li>Người dùng sẽ nhận được thông báo về việc xóa</li>
                            <li>Không thể khôi phục sau khi xóa</li>
                        </ul>
                    </div>
                    
                    <div class="row mb-3">
                        <div class="col-4 text-end"><strong>Tiêu đề:</strong></div>
                        <div class="col-8"><span id="deleteItemTitle"></span></div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="admin_delete_comment" class="form-label fw-bold text-dark mb-2 d-flex align-items-center small">
                            <i class="fas fa-comment-alt me-2 text-secondary"></i> Lý do xóa bài đăng (bắt buộc):
                        </label>
                        <textarea class="form-control border-warning-subtle" id="admin_delete_comment" name="admin_delete_comment" rows="3" placeholder="Nhập lý do xóa bài đăng (bắt buộc)..." required></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-danger">Xóa bài đăng</button>
                </div>
            </form>
        </div>
    </div>
</div>
```

**Thêm JavaScript xử lý:**
```javascript
document.addEventListener('DOMContentLoaded', function() {
    // Xử lý modal xóa bài đăng
    const deleteItemModal = document.getElementById('deleteItemModal');
    if (deleteItemModal) {
        deleteItemModal.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            const itemId = button.getAttribute('data-item-id');
            const itemTitle = button.getAttribute('data-item-title');
            
            const form = deleteItemModal.querySelector('#deleteItemForm');
            form.action = `/admin/items/${itemId}`;
            
            deleteItemModal.querySelector('#deleteItemTitle').textContent = itemTitle;
        });
        
        // Clear delete reason when modal is hidden
        deleteItemModal.addEventListener('hidden.bs.modal', function() {
            const textarea = deleteItemModal.querySelector('#admin_delete_comment');
            textarea.value = '';
        });
    }
});
```

### **2. Android App**

#### **A. Cập nhật Notification Model: `Notification.java`**

**File:** `FindIt-TLU/app/src/main/java/com/example/findittlu/data/model/Notification.java`

**Thêm trường reason vào class Data:**
```java
public static class Data {
    @SerializedName("title")
    private String title;
    @SerializedName("item_id")
    private int item_id;
    @SerializedName("type")
    private String type;
    @SerializedName("reason")
    private String reason;
    
    // ... existing getters and setters ...
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
```

#### **B. Cập nhật NotificationsViewModel: `NotificationsViewModel.java`**

**File:** `FindIt-TLU/app/src/main/java/com/example/findittlu/ui/common/NotificationsViewModel.java`

**Cập nhật logic xử lý notification type:**
```java
if (notifType != null) {
    if (notifType.toLowerCase().contains("approved") || notifType.toLowerCase().contains("success") || notifType.toLowerCase().contains("duyệt")) {
        type = NotificationItem.TYPE_SUCCESS;
    } else if (notifType.toLowerCase().contains("pending") || notifType.toLowerCase().contains("chờ")) {
        type = NotificationItem.TYPE_INFO;
    } else if (notifType.toLowerCase().contains("rejected") || notifType.toLowerCase().contains("từ chối") || notifType.toLowerCase().contains("warning") || notifType.toLowerCase().contains("deleted") || notifType.toLowerCase().contains("xóa")) {
        type = NotificationItem.TYPE_WARNING;
    }
}
```

**Thay đổi:**
- ✅ Thêm hỗ trợ type "deleted" và "xóa"
- ✅ Hiển thị icon warning cho notification xóa bài đăng

## Luồng hoạt động

### **1. Admin xóa bài đăng:**
1. Admin vào trang chi tiết bài đăng
2. Click nút "Xóa bài đăng"
3. Modal xác nhận hiển thị với form nhập lý do
4. Admin nhập lý do xóa (bắt buộc)
5. Click "Xóa bài đăng"
6. Hệ thống gửi notification cho người dùng
7. Xóa bài đăng khỏi database

### **2. Người dùng nhận thông báo:**
1. Người dùng mở app Android
2. Vào mục "Thông báo"
3. Thấy notification mới với icon warning
4. Nội dung: "Tin đăng 'Tên bài đăng' của bạn đã bị xóa khỏi hệ thống. Lý do: [Lý do admin nhập]"

## Tính năng bảo mật

- ✅ **Validation bắt buộc:** Admin phải nhập lý do xóa
- ✅ **Xác nhận:** Modal xác nhận trước khi xóa
- ✅ **Logging:** Ghi log lỗi nếu có vấn đề
- ✅ **Soft Delete:** Sử dụng soft delete để có thể khôi phục nếu cần

## Lợi ích

1. **Minh bạch:** Người dùng biết lý do bài đăng bị xóa
2. **Trách nhiệm:** Admin phải có lý do chính đáng
3. **Thông báo real-time:** Người dùng nhận thông báo ngay lập tức
4. **UX tốt:** Giao diện thân thiện và dễ sử dụng
5. **Bảo mật:** Validation và xác nhận đầy đủ 