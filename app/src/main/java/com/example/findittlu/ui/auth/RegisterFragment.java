package com.example.findittlu.ui.auth;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.findittlu.R;
import com.example.findittlu.viewmodel.RegisterViewModel;
import com.example.findittlu.data.model.LoginResponse;
import com.example.findittlu.utils.CustomToast;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterFragment extends Fragment {
    private RegisterViewModel registerViewModel;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        // Sự kiện chuyển sang giao diện đăng nhập
        view.findViewById(R.id.loginTextView).setOnClickListener(v -> {
            androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(requireActivity(), com.example.findittlu.R.id.nav_host_fragment);
            navController.navigate(com.example.findittlu.R.id.loginFragment);
        });

        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        EditText nameEditText = view.findViewById(R.id.fullNameEditText);
        EditText emailEditText = view.findViewById(R.id.emailEditText);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);
        EditText confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        EditText phoneEditText = view.findViewById(R.id.phoneEditText);

        TextInputLayout nameInputLayout = view.findViewById(R.id.nameInputLayout);
        TextInputLayout emailInputLayout = view.findViewById(R.id.emailInputLayout);
        TextInputLayout passwordInputLayout = view.findViewById(R.id.passwordInputLayout);
        TextInputLayout confirmPasswordInputLayout = view.findViewById(R.id.confirmPasswordInputLayout);
        TextInputLayout phoneInputLayout = view.findViewById(R.id.phoneInputLayout);

        view.findViewById(R.id.registerButton).setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            boolean hasError = false;
            Drawable errorIcon = getResources().getDrawable(R.drawable.ic_warning, null);
            errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());
            if (name.isEmpty()) {
                nameInputLayout.setError("Bắt buộc");
                hasError = true;
            } else {
                nameInputLayout.setError(null);
            }
            if (email.isEmpty()) {
                emailInputLayout.setError("Bắt buộc");
                hasError = true;
            } else {
                emailInputLayout.setError(null);
            }
            if (password.isEmpty()) {
                passwordInputLayout.setError("Bắt buộc");
                hasError = true;
            } else {
                passwordInputLayout.setError(null);
            }
            if (confirmPassword.isEmpty()) {
                confirmPasswordInputLayout.setError("Bắt buộc");
                hasError = true;
            } else {
                confirmPasswordInputLayout.setError(null);
            }
            if (phone.isEmpty()) {
                phoneInputLayout.setError("Bắt buộc");
                hasError = true;
            } else {
                phoneInputLayout.setError(null);
            }
            if (hasError) return;
            if (!password.equals(confirmPassword)) {
                confirmPasswordInputLayout.setError("Mật khẩu xác nhận không khớp");
                return;
            } else {
                confirmPasswordInputLayout.setError(null);
            }
            registerViewModel.register(name, email, password, confirmPassword, phone).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.getUser() != null) {
                    CustomToast.showCustomToast(getContext(), "Đăng ký thành công", "Vui lòng đăng nhập.");
                    androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(requireActivity(), com.example.findittlu.R.id.nav_host_fragment);
                    navController.navigate(com.example.findittlu.R.id.loginFragment);
                } else {
                    CustomToast.showCustomToast(getContext(), "Đăng ký thất bại", "Kiểm tra lại thông tin.");
                }
            });
        });

        // Thêm TextWatcher để clear lỗi khi nhập lại
        nameEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameInputLayout.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
        emailEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailInputLayout.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
        passwordEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordInputLayout.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
        confirmPasswordEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmPasswordInputLayout.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
        phoneEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneInputLayout.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        return view;
    }
}
