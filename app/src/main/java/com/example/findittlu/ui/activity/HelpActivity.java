package com.example.findittlu.ui.activity; // Thay thế bằng package name của bạn

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findittlu.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HelpActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private ViewGroup mainContentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Ánh xạ các view
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        mainContentLayout = findViewById(R.id.main_content_layout);

        // Thiết lập Toolbar
        toolbar.setNavigationOnClickListener(v -> finish());

        // Thiết lập các mục FAQ
        setupFaqItems();

        // Thiết lập Bottom Navigation
        setupBottomNavigation();
    }

    /**
     * Thiết lập dữ liệu và sự kiện click cho các mục FAQ.
     */
    private void setupFaqItems() {
        // FAQ 1
        View faq1 = findViewById(R.id.faq1);
        setupSingleFaq(faq1, getString(R.string.help_q1), getString(R.string.help_a1));

        // FAQ 2
        View faq2 = findViewById(R.id.faq2);
        setupSingleFaq(faq2, getString(R.string.help_q2), getString(R.string.help_a2));

        // FAQ 3
        View faq3 = findViewById(R.id.faq3);
        setupSingleFaq(faq3, getString(R.string.help_q3), getString(R.string.help_a3));

        // FAQ 4
        View faq4 = findViewById(R.id.faq4);
        setupSingleFaq(faq4, getString(R.string.help_q4), getString(R.string.help_a4));

        // FAQ 5
        View faq5 = findViewById(R.id.faq5);
        setupSingleFaq(faq5, getString(R.string.help_q5), getString(R.string.help_a5));
    }

    /**
     * Helper method để cấu hình cho một mục FAQ.
     * @param faqView View gốc của mục FAQ (từ layout đã include).
     * @param question Chuỗi câu hỏi.
     * @param answer Chuỗi câu trả lời.
     */
    private void setupSingleFaq(View faqView, String question, String answer) {
        TextView questionTextView = faqView.findViewById(R.id.faq_question_text);
        TextView answerTextView = faqView.findViewById(R.id.faq_answer_text);
        ImageView toggleIcon = faqView.findViewById(R.id.faq_toggle_icon);
        RelativeLayout questionLayout = faqView.findViewById(R.id.question_layout);

        // Gán văn bản
        questionTextView.setText(question);
        answerTextView.setText(answer);

        // Thiết lập sự kiện click
        questionLayout.setOnClickListener(v -> {
            // Hiệu ứng animation mượt mà
            TransitionManager.beginDelayedTransition(mainContentLayout);

            if (answerTextView.getVisibility() == View.GONE) {
                // Mở câu trả lời
                answerTextView.setVisibility(View.VISIBLE);
                toggleIcon.animate().rotation(180f).setDuration(300).start();
            } else {
                // Đóng câu trả lời
                answerTextView.setVisibility(View.GONE);
                toggleIcon.animate().rotation(0f).setDuration(300).start();
            }
        });
    }

    /**
     * Cấu hình BottomNavigationView.
     */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_post) {
                startActivity(new Intent(getApplicationContext(), PostItemActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            // Thêm điều hướng cho các mục khác nếu cần
            // else if (itemId == R.id.navigation_home) { ... }

            return itemId == R.id.navigation_profile;
        });
    }
}
