package com.example.findittlu.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.bumptech.glide.Glide;
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

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        // Thiết lập Toolbar
        setupToolbar(view, navController);

        // Thiết lập các nút
        setupButtons(view, navController);

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
                        avatarImageView.setImageURI(uri); // Preview
                        // Upload lên server
                        userViewModel.uploadAvatar(requireContext(), uri).observe(getViewLifecycleOwner(), user -> {
                            if (user != null && user.getPhotoUrl() != null) {
                                CustomToast.showCustomToast(getContext(), "Thành công", "Đổi ảnh đại diện thành công!");
                                ImageUtils.loadAvatar(requireContext(), user.getPhotoUrl(), avatarImageView);
                            } else {
                                // Có thể API trả về body rỗng nhưng thực tế đã thành công
                                userViewModel.getProfile().observe(getViewLifecycleOwner(), refreshedUser -> {
                                    if (refreshedUser != null && refreshedUser.getPhotoUrl() != null) {
                                        ImageUtils.loadAvatar(requireContext(), refreshedUser.getPhotoUrl(), avatarImageView);
                                        CustomToast.showCustomToast(getContext(), "Thành công", "Đổi ảnh đại diện thành công!");
                                    } else {
                                        CustomToast.showCustomToast(getContext(), "Thất bại", "Đổi ảnh đại diện thất bại!");
                                    }
                                });
                            }
                        });
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
                if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                    ImageUtils.loadAvatar(requireContext(), user.getPhotoUrl(), avatarImageView);
                } else {
                    avatarImageView.setImageResource(R.drawable.ic_person);
                }
            }
        });

        // Lưu thay đổi
        MaterialButton saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            CustomToast.showCustomToast(getContext(), "Lưu thay đổi", "Đã lưu thay đổi");
            // Sau khi lưu, có thể quay lại màn hình Profile
            navController.popBackStack();
        });
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

    private void setupButtons(View view, NavController navController) {
        MaterialButton saveButton = view.findViewById(R.id.saveButton);
        MaterialButton cancelButton = view.findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(v -> {
            CustomToast.showCustomToast(getContext(), "Lưu thay đổi", "Đã lưu thay đổi");
            // Sau khi lưu, có thể quay lại màn hình Profile
            navController.popBackStack();
        });

        cancelButton.setOnClickListener(v -> {
            navController.popBackStack();
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickAvatarLauncher.launch(Intent.createChooser(intent, "Chọn ảnh đại diện"));
    }
} 