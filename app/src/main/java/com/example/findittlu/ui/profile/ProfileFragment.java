package com.example.findittlu.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.findittlu.R;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);

        // Thiết lập Toolbar
        setupToolbar(view);

        // Thiết lập các mục có thể click
        setupMenuItems(view, navController);
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
    }

    private void setupMenuItems(View view, NavController navController) {
        TextView personalInfo = view.findViewById(R.id.personalInfoTextView);
        TextView myPosts = view.findViewById(R.id.myPostsTextView);
        TextView logout = view.findViewById(R.id.logoutTextView);
        TextView policy = view.findViewById(R.id.policyTextView);
        TextView help = view.findViewById(R.id.helpTextView);

        personalInfo.setOnClickListener(v -> {
            // Điều hướng đến màn hình Chỉnh sửa thông tin cá nhân
            navController.navigate(R.id.action_profileFragment_to_editProfileFragment);
        });
        myPosts.setOnClickListener(v -> {
            // Điều hướng đến màn hình Tin đăng của tôi
            navController.navigate(R.id.action_profileFragment_to_myPostsFragment);
        });
        policy.setOnClickListener(v -> {
            // Điều hướng đến màn hình Chính sách
            navController.navigate(R.id.action_profileFragment_to_policyFragment);
        });
        help.setOnClickListener(v -> {
            // Điều hướng đến màn hình Trợ giúp
            navController.navigate(R.id.action_profileFragment_to_helpFragment);
        });
        logout.setOnClickListener(v -> {
            // Xử lý logic đăng xuất
            SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
            Toast.makeText(getContext(), "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.loginFragment);
        });
    }
} 