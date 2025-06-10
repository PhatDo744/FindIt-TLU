package com.example.findittlu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PostItemActivity extends AppCompatActivity {

    private EditText dateEditText, titleEditText;
    private Spinner categorySpinner;
    private TextView dateLabelTextView;
    private MaterialButton lostItemButton, foundItemButton;
    private boolean isFoundSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

        // Khởi tạo các view
        initViews();

        // Thiết lập Toolbar
        setupToolbar();

        // Thiết lập Bottom Navigation
        setupBottomNavigation();

        // Thiết lập các listener và UI ban đầu
        setupDatePicker();
        setupCategorySpinner();
        setupPostTypeButtons();
    }

    private void initViews() {
        dateEditText = findViewById(R.id.dateEditText);
        titleEditText = findViewById(R.id.titleEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        dateLabelTextView = findViewById(R.id.dateLabelTextView);
        lostItemButton = findViewById(R.id.lostItemButton);
        foundItemButton = findViewById(R.id.foundItemButton);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    // ================== PHẦN ĐƯỢC CẬP NHẬT ==================
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Đánh dấu mục hiện tại là "Đăng tin"
        bottomNavigationView.setSelectedItemId(R.id.navigation_post);

        // Lắng nghe sự kiện click trên thanh điều hướng
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_profile) {
                // Nếu nhấn vào "Cá nhân", chuyển sang ProfileActivity
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                // Xóa hiệu ứng chuyển cảnh mặc định
                overridePendingTransition(0, 0);
                return true;
            }
            // Thêm các trường hợp khác nếu cần (ví dụ: Trang chủ, Tìm kiếm...)
            // else if (itemId == R.id.navigation_home) { ... }

            // Nếu nhấn vào mục hiện tại, không làm gì cả
            return itemId == R.id.navigation_post;
        });
    }
    // =======================================================


    private void setupDatePicker() {
        dateEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    PostItemActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year1, monthOfYear, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dateEditText.setText(sdf.format(selectedDate.getTime()));
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupCategorySpinner() {
        String[] categories = new String[]{"Chọn danh mục", "Đồ điện tử", "Giấy tờ", "Ví/Túi", "Quần áo", "Chìa khóa", "Khác"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
                } else {
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.text_primary));
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void setupPostTypeButtons() {
        updateButtonSelection();

        lostItemButton.setOnClickListener(v -> {
            if (isFoundSelected) {
                isFoundSelected = false;
                updateButtonSelection();
            }
        });

        foundItemButton.setOnClickListener(v -> {
            if (!isFoundSelected) {
                isFoundSelected = true;
                updateButtonSelection();
            }
        });
    }

    private void updateButtonSelection() {
        foundItemButton.setSelected(isFoundSelected);
        lostItemButton.setSelected(!isFoundSelected);
        updateHintsAndLabels();
    }

    private void updateHintsAndLabels() {
        if (!isFoundSelected) {
            dateLabelTextView.setText(getString(R.string.date_lost_label));
        } else {
            dateLabelTextView.setText(getString(R.string.date_found_label));
        }
    }
}
