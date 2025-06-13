package com.example.findittlu.ui.common;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.findittlu.R;
import com.google.android.material.appbar.MaterialToolbar;

public class HelpFragment extends Fragment {

    private ViewGroup mainContentLayout;

    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_help, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);

        // Ánh xạ các view
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        mainContentLayout = view.findViewById(R.id.main_content_layout);

        // Thiết lập Toolbar
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        // Thiết lập các mục FAQ
        setupFaqItems(view);
    }

    private void setupFaqItems(View view) {
        // FAQ 1
        View faq1 = view.findViewById(R.id.faq1);
        setupSingleFaq(faq1, getString(R.string.help_q1), getString(R.string.help_a1));

        // FAQ 2
        View faq2 = view.findViewById(R.id.faq2);
        setupSingleFaq(faq2, getString(R.string.help_q2), getString(R.string.help_a2));

        // FAQ 3
        View faq3 = view.findViewById(R.id.faq3);
        setupSingleFaq(faq3, getString(R.string.help_q3), getString(R.string.help_a3));

        // FAQ 4
        View faq4 = view.findViewById(R.id.faq4);
        setupSingleFaq(faq4, getString(R.string.help_q4), getString(R.string.help_a4));

        // FAQ 5
        View faq5 = view.findViewById(R.id.faq5);
        setupSingleFaq(faq5, getString(R.string.help_q5), getString(R.string.help_a5));
    }

    private void setupSingleFaq(View faqView, String question, String answer) {
        TextView questionTextView = faqView.findViewById(R.id.faq_question_text);
        TextView answerTextView = faqView.findViewById(R.id.faq_answer_text);
        ImageView toggleIcon = faqView.findViewById(R.id.faq_toggle_icon);
        RelativeLayout questionLayout = faqView.findViewById(R.id.question_layout);

        questionTextView.setText(question);
        answerTextView.setText(answer);

        questionLayout.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(mainContentLayout);

            if (answerTextView.getVisibility() == View.GONE) {
                answerTextView.setVisibility(View.VISIBLE);
                toggleIcon.animate().rotation(180f).setDuration(300).start();
            } else {
                answerTextView.setVisibility(View.GONE);
                toggleIcon.animate().rotation(0f).setDuration(300).start();
            }
        });
    }
} 