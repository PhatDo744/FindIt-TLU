package com.example.findittlu.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.lifecycle.ViewModelProvider;

import com.example.findittlu.R;
import com.google.android.material.button.MaterialButton;
import com.example.findittlu.viewmodel.UserViewModel;
import com.example.findittlu.data.model.User;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.findittlu.utils.ImageUtils;
import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.widget.TextView;
import com.example.findittlu.utils.CustomToast;

public class EditProfileFragment extends Fragment {

    private ImageView avatarImageView;
    private ImageView cameraIcon;
    private TextView changeAvatarTextView;
    private ActivityResultLauncher<Intent> pickAvatarLauncher;
    private UserViewModel userViewModel;
    private Uri selectedAvatarUri;
    private boolean hasAvatarChanged = false; // Flag để kiểm tra ảnh có thay đổi không
    private String originalAvatarUrl; // Lưu URL ảnh gốc

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        // Thiết lập Toolbar
        setupToolbar(view, navController);

        // Khởi tạo view và ViewModel
        avatarImageView = view.findViewById(R.id.avatarImageView);
        cameraIcon = view.findViewById(R.id.cameraIcon);
        changeAvatarTextView = view.findViewById(R.id.changeAvatarTextView);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Đăng ký ActivityResultLauncher để chọn ảnh
        pickAvatarLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        selectedAvatarUri = uri;
                        hasAvatarChanged = true; // Đánh dấu ảnh đã thay đổi
                        avatarImageView.setImageURI(uri); // Chỉ hiển thị preview, không upload
                        CustomToast.showCustomToast(getContext(), "Thông báo", "Ảnh đã được chọn. Nhấn 'Lưu' để cập nhật!");
                    }
                }
            });

        // Bắt sự kiện click vào icon bút chì/camera và text đổi avatar
        cameraIcon.setOnClickListener(v -> openImagePicker());
        changeAvatarTextView.setOnClickListener(v -> openImagePicker());

        // Lấy dữ liệu user và bind lên UI
        EditText fullNameEditText = view.findViewById(R.id.fullNameEditText);
        EditText phoneEditText = view.findViewById(R.id.phoneEditText);
        EditText emailEditText = view.findViewById(R.id.emailEditText);
        
        userViewModel.getProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                fullNameEditText.setText(user.getFullName());
                phoneEditText.setText(user.getPhoneNumber());
                emailEditText.setText(user.getEmail());
                originalAvatarUrl = user.getPhotoUrl(); // Lưu URL ảnh gốc
                if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                    ImageUtils.loadAvatar(requireContext(), user.getPhotoUrl(), avatarImageView);
                } else {
                    avatarImageView.setImageResource(R.drawable.ic_person);
                }
            }
        });

        // Thiết lập các nút
        setupButtons(view, navController, fullNameEditText, phoneEditText, emailEditText);
    }

    private void setupToolbar(View view, NavController navController) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            navController.popBackStack();
        });
    }

    private void setupButtons(View view, NavController navController, EditText fullNameEditText, EditText phoneEditText, EditText emailEditText) {
        MaterialButton saveButton = view.findViewById(R.id.saveButton);
        MaterialButton cancelButton = view.findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(v -> {
            String fullName = fullNameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();

            // Kiểm tra dữ liệu đầu vào
            if (fullName.isEmpty()) {
                CustomToast.showCustomToast(getContext(), "Lỗi", "Vui lòng nhập họ tên!");
                return;
            }

            if (phone.isEmpty()) {
                CustomToast.showCustomToast(getContext(), "Lỗi", "Vui lòng nhập số điện thoại!");
                return;
            }

            // Disable nút để tránh click nhiều lần
            saveButton.setEnabled(false);
            saveButton.setText("Đang lưu...");

            // Nếu có ảnh mới được chọn, upload ảnh trước
            if (hasAvatarChanged && selectedAvatarUri != null) {
                userViewModel.uploadAvatar(requireContext(), selectedAvatarUri).observe(getViewLifecycleOwner(), user -> {
                    // Luôn tiếp tục cập nhật thông tin, bất kể kết quả upload ảnh
                    // Vì API có thể đã upload thành công ngay cả khi trả về null
                    updateUserProfile(fullName, phone, email, navController, saveButton);
                });
            } else {
                // Không có ảnh mới, chỉ cập nhật thông tin
                updateUserProfile(fullName, phone, email, navController, saveButton);
            }
        });

        cancelButton.setOnClickListener(v -> {
            // Nếu có ảnh đã thay đổi, khôi phục lại ảnh cũ
            if (hasAvatarChanged && originalAvatarUrl != null) {
                ImageUtils.loadAvatar(requireContext(), originalAvatarUrl, avatarImageView);
            }
            navController.popBackStack();
        });
    }

    private void updateUserProfile(String fullName, String phone, String email, NavController navController, MaterialButton saveButton) {
        User user = new User();
        user.setFullName(fullName);
        user.setPhoneNumber(phone);
        user.setEmail(email);

        userViewModel.updateProfile(user).observe(getViewLifecycleOwner(), updatedUser -> {
            saveButton.setEnabled(true);
            saveButton.setText("Lưu thay đổi");
            
            if (updatedUser != null) {
                String message = "Cập nhật thông tin thành công!";
                if (hasAvatarChanged) {
                    message += " (bao gồm ảnh đại diện)";
                }
                CustomToast.showCustomToast(getContext(), "Thành công", message);
                navController.popBackStack();
            } else {
                CustomToast.showCustomToast(getContext(), "Thất bại", "Cập nhật thông tin thất bại!");
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickAvatarLauncher.launch(Intent.createChooser(intent, "Chọn ảnh đại diện"));
    }
} 