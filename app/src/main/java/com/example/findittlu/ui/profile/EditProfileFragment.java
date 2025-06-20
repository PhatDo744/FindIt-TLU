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

public class EditProfileFragment extends Fragment {

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

        // Lấy dữ liệu user và bind lên UI
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        EditText fullNameEditText = view.findViewById(R.id.fullNameEditText);
        EditText phoneEditText = view.findViewById(R.id.phoneEditText);
        EditText emailEditText = view.findViewById(R.id.emailEditText);
        ImageView avatarImageView = view.findViewById(R.id.avatarImageView);
        userViewModel.getProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                fullNameEditText.setText(user.getFullName());
                phoneEditText.setText(user.getPhoneNumber());
                emailEditText.setText(user.getEmail());
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

        // Lưu thay đổi
        MaterialButton saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            String newName = fullNameEditText.getText().toString().trim();
            String newPhone = phoneEditText.getText().toString().trim();
            User updateUser = new User();
            updateUser.setFullName(newName);
            updateUser.setPhoneNumber(newPhone);
            userViewModel.updateProfile(updateUser).observe(getViewLifecycleOwner(), updatedUser -> {
                if (updatedUser != null) {
                    Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    navController.popBackStack();
                } else {
                    Toast.makeText(getContext(), "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupToolbar(View view, NavController navController) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
    }

    private void setupButtons(View view, NavController navController) {
        MaterialButton saveButton = view.findViewById(R.id.saveButton);
        MaterialButton cancelButton = view.findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Lưu thay đổi", Toast.LENGTH_SHORT).show();
            // Sau khi lưu, có thể quay lại màn hình Profile
            navController.popBackStack();
        });

        cancelButton.setOnClickListener(v -> {
            navController.popBackStack();
        });
    }
} 