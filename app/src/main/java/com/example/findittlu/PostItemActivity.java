package com.example.findittlu;

import android.app.DatePickerDialog;
import android.os.Bundle;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PostItemActivity extends AppCompatActivity {

    private EditText dateEditText;
    private Spinner categorySpinner;
    private TextView dateLabelTextView;
    private EditText titleEditText;
    private MaterialButton lostItemButton, foundItemButton;
    private boolean isFoundSelected = true; // Bắt đầu với "Nhặt đồ" được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

        // Khởi tạo các view
        initViews();

        // Thiết lập Toolbar
        setupToolbar();

        // Thiết lập Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_post);

        // Thiết lập các listener và UI ban đầu
        setupDatePicker();
        setupCategorySpinner();
        setupPostTypeButtons();
    }

    private void initViews() {
        dateEditText = findViewById(R.id.dateEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        dateLabelTextView = findViewById(R.id.dateLabelTextView);
        titleEditText = findViewById(R.id.titleEditText);
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
        // Thiết lập trạng thái ban đầu
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
        // Chỉ cần cập nhật trạng thái selected, XML selectors sẽ tự động xử lý giao diện
        foundItemButton.setSelected(isFoundSelected);
        lostItemButton.setSelected(!isFoundSelected);
        updateHintsAndLabels();
    }

    private void updateHintsAndLabels() {
        if (!isFoundSelected) { // Tức là Mất đồ được chọn
            dateLabelTextView.setText("Ngày mất*");
            titleEditText.setHint("VD: Mất ví màu đen gần thư viện");
        } else { // Tức là Nhặt đồ được chọn
            dateLabelTextView.setText("Ngày nhặt được*");
            titleEditText.setHint("VD: Nhặt được ví màu đen ở thư viện");
        }
    }
}
