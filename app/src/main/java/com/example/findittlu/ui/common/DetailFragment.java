package com.example.findittlu.ui.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.findittlu.R;
import androidx.lifecycle.ViewModelProvider;
import android.widget.Toast;
import com.example.findittlu.utils.ImageUtils;
import com.example.findittlu.utils.CustomToast;

public class DetailFragment extends Fragment {
    private DetailViewModel detailViewModel;

    public DetailFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        Bundle args = getArguments();
        long postId = args != null && args.containsKey("postId") ? args.getLong("postId", 1) : 1;
        String imageUrl = args != null ? args.getString("imageUrl", null) : null;
        ImageView imageView = view.findViewById(R.id.detail_image);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            com.example.findittlu.utils.ImageUtils.loadItemImage(requireContext(), imageUrl, imageView);
        }
        detailViewModel.getIsApiConnected().observe(getViewLifecycleOwner(), connected -> {
            if (!connected) CustomToast.showCustomToast(getContext(), "Lỗi kết nối", "Không thể kết nối API!");
        });
        detailViewModel.getDetailData().observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;
            ((TextView) view.findViewById(R.id.detail_title)).setText(data.title);
            ((TextView) view.findViewById(R.id.detail_desc)).setText(data.description);
            ((TextView) view.findViewById(R.id.detail_date)).setText(data.date);
            ((TextView) view.findViewById(R.id.detail_time_ago)).setText(data.timeAgo);
            ((TextView) view.findViewById(R.id.detail_location)).setText(data.location);
            ((TextView) view.findViewById(R.id.detail_status)).setText(data.isLost ? "Đồ vật bị mất" : "Đồ vật đã tìm thấy");
            ((TextView) view.findViewById(R.id.detail_user_name)).setText(data.userName);
            if ((imageUrl == null || imageUrl.isEmpty()) && data.imageUrl != null && !data.imageUrl.isEmpty()) {
                com.example.findittlu.utils.ImageUtils.loadItemImage(requireContext(), data.imageUrl, imageView);
            }
        });
        detailViewModel.fetchDetail(postId);
        Button btnContact = view.findViewById(R.id.btn_contact);
        btnContact.setOnClickListener(v -> {
            // TODO: Xử lý logic liên hệ (ví dụ mở email hoặc gọi điện)
        });
    }
} 