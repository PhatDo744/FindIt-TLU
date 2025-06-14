package com.example.findittlu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findittlu.R;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String location = intent.getStringExtra("location");
        String date = intent.getStringExtra("date");
        boolean isLost = intent.getBooleanExtra("isLost", true);
        int imageRes = intent.getIntExtra("imageRes", R.drawable.image_placeholder_background);

        // Gán dữ liệu vào view
        ImageView imageView = findViewById(R.id.detail_image);
        TextView statusView = findViewById(R.id.detail_status);
        TextView titleView = findViewById(R.id.detail_title);
        TextView dateView = findViewById(R.id.detail_date);
        TextView locationView = findViewById(R.id.detail_location);
        // ... các view khác

        imageView.setImageResource(imageRes);
        statusView.setText(isLost ? "Đồ vật bị mất" : "Đồ vật đã tìm thấy");
        titleView.setText(title);
        dateView.setText(date);
        locationView.setText(location);
        // ... gán các trường khác tương tự

        Button btnContact = findViewById(R.id.btn_contact);
        btnContact.setOnClickListener(v -> {
            // Xử lý logic liên hệ, ví dụ mở email hoặc gọi điện
        });
    }
} 