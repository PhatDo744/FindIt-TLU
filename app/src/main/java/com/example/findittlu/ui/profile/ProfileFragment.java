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
import com.example.findittlu.utils.ImageUtils;
import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.findittlu.R;

public class ProfileFragment extends Fragment {

    private ImageView avatarImageView;
    private ImageView editIcon;
    private ActivityResultLauncher<Intent> pickAvatarLauncher;
    private UserViewModel userViewModel;
    private Uri selectedAvatarUri;

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

        // Khởi tạo view và ViewModel
        avatarImageView = view.findViewById(R.id.avatarImageView);
        editIcon = view.findViewById(R.id.editIcon);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Đăng ký ActivityResultLauncher để chọn ảnh
        pickAvatarLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        selectedAvatarUri = uri;
                        avatarImageView.setImageURI(uri); // Preview
                        // Upload lên server
                        userViewModel.uploadAvatar(requireContext(), uri).observe(getViewLifecycleOwner(), user -> {
                            if (user != null && user.getPhotoUrl() != null) {
                                Toast.makeText(getContext(), "Đổi ảnh đại diện thành công!", Toast.LENGTH_SHORT).show();
                                ImageUtils.loadAvatar(requireContext(), user.getPhotoUrl(), avatarImageView);
                            } else {
                                // Có thể API trả về body rỗng nhưng thực tế đã thành công
                                userViewModel.getProfile().observe(getViewLifecycleOwner(), refreshedUser -> {
                                    if (refreshedUser != null && refreshedUser.getPhotoUrl() != null) {
                                        ImageUtils.loadAvatar(requireContext(), refreshedUser.getPhotoUrl(), avatarImageView);
                                        Toast.makeText(getContext(), "Đổi ảnh đại diện thành công!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Đổi ảnh đại diện thất bại!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });

        // Bắt sự kiện click vào icon camera
        editIcon.setOnClickListener(v -> openImagePicker());

        // Thiết lập các mục có thể click
        setupMenuItems(view, navController);

        // Lấy và hiển thị thông tin user
        userViewModel.getProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                TextView nameTextView = view.findViewById(R.id.userNameTextView);
                TextView joinDateTextView = view.findViewById(R.id.joinDateTextView);
                nameTextView.setText(user.getFullName());
                // Hiển thị ngày tham gia nếu có trường createdAt
                if (user.getCreatedAt() != null && !user.getCreatedAt().isEmpty()) {
                    String date = user.getCreatedAt().split("T")[0];
                    String[] parts = date.split("-");
                    if (parts.length >= 2) {
                        joinDateTextView.setText("Thành viên từ " + parts[1] + "/" + parts[0]);
                    } else {
                        joinDateTextView.setText("Thành viên từ " + date);
                    }
                }
                if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                    ImageUtils.loadAvatar(requireContext(), user.getPhotoUrl(), avatarImageView);
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

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickAvatarLauncher.launch(Intent.createChooser(intent, "Chọn ảnh đại diện"));
    }
} 