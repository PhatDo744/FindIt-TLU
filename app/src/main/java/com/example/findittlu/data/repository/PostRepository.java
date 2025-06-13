package com.example.findittlu.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.findittlu.data.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PostRepository {
    private List<Post> allPosts;

    public PostRepository() {
        // Khởi tạo dữ liệu mẫu
        createDummyData();
    }

    // Giả lập việc lấy dữ liệu từ API hoặc DB
    public LiveData<List<Post>> getPosts() {
        MutableLiveData<List<Post>> data = new MutableLiveData<>();
        data.setValue(allPosts);
        return data;
    }

    public LiveData<List<Post>> getPostsByType(String type) {
        MutableLiveData<List<Post>> data = new MutableLiveData<>();
        if (type.equals("ALL")) {
            data.setValue(allPosts);
        } else {
            List<Post> filteredPosts = allPosts.stream()
                                               .filter(p -> p.getStatus().equalsIgnoreCase(type))
                                               .collect(Collectors.toList());
            data.setValue(filteredPosts);
        }
        return data;
    }

    public void createPost(Post post) {
        // Giả lập việc thêm bài đăng mới vào danh sách
        allPosts.add(0, post); // Thêm vào đầu danh sách
    }

    private void createDummyData() {
        allPosts = new ArrayList<>();
        allPosts.add(new Post("Mất thẻ sinh viên khu giảng đường A2", "Ngày đăng: 22/05/2025", "SEARCHING"));
        allPosts.add(new Post("Nhặt được tai nghe Bluetooth màu trắng ở thư viện", "Ngày đăng: 21/05/2025", "FOUND"));
        allPosts.add(new Post("Tìm thấy chìa khóa xe máy gần nhà xe C1", "Ngày đăng: 20/05/2025", "FOUND"));
        allPosts.add(new Post("Đã tìm lại được ví ở căng tin", "Ngày đăng: 19/05/2025", "COMPLETED"));
        allPosts.add(new Post("Mất sách Giải tích 1 tại phòng tự học B101", "Ngày đăng: 18/05/2025", "SEARCHING"));
    }
}
