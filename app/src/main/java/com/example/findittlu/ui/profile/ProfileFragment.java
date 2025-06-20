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
import androidx.lifecycle.ViewModelProvider;
import com.example.findittlu.viewmodel.UserViewModel;
import com.example.findittlu.data.model.User;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

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

        // Lấy và hiển thị thông tin user
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                TextView nameTextView = view.findViewById(R.id.userNameTextView);
                TextView joinDateTextView = view.findViewById(R.id.joinDateTextView);
                ImageView avatarImageView = view.findViewById(R.id.avatarImageView);
                nameTextView.setText(user.getFullName());
                // Hiển thị ngày tham gia nếu có trường createdAt
                if (user.getCreatedAt() != null && !user.getCreatedAt().isEmpty()) {
                    // Giả sử createdAt là yyyy-MM-dd hoặc yyyy-MM-ddTHH:mm:ssZ
                    String date = user.getCreatedAt().split("T")[0];
                    String[] parts = date.split("-");
                    if (parts.length >= 2) {
                        joinDateTextView.setText("Thành viên từ " + parts[1] + "/" + parts[0]);
                    } else {
                        joinDateTextView.setText("Thành viên từ " + date);
                    }
                }
                if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                    String url = user.getPhotoUrl();
                    if (!url.startsWith("http")) {
                        url = "http://10.0.2.2:8000" + url;
                    }
                    Glide.with(this).load(url).placeholder(R.drawable.ic_person).into(avatarImageView);
                } else {
                    avatarImageView.setImageResource(R.drawable.ic_person);
                }
            }
        });
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