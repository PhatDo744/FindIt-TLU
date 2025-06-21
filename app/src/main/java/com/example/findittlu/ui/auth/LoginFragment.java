package com.example.findittlu.ui.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.findittlu.R;
import com.example.findittlu.viewmodel.LoginViewModel;
import com.example.findittlu.utils.CustomToast;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {
    private LoginViewModel loginViewModel;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);

        // Kiểm tra nếu đã đăng nhập thì chuyển sang HomeFragment
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        if (userId != -1) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.homeFragment);
            return view;
        }

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        EditText emailEditText = view.findViewById(R.id.emailEditText);
        CheckBox rememberEmailCheckBox = view.findViewById(R.id.rememberEmailCheckBox);
        TextInputLayout emailInputLayout = view.findViewById(R.id.emailInputLayout);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);
        TextInputLayout passwordInputLayout = view.findViewById(R.id.passwordInputLayout);

        // Lấy email đã lưu (nếu có)
        String savedEmail = prefs.getString("remembered_email", "");
        if (!savedEmail.isEmpty()) {
            emailEditText.setText(savedEmail);
            rememberEmailCheckBox.setChecked(true);
        }

        // Xử lý nút đăng nhập
        view.findViewById(R.id.loginButton).setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            boolean hasError = false;
            Drawable errorIcon = getResources().getDrawable(R.drawable.ic_warning, null);
            errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());
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
            if (hasError) return;
            // Lưu hoặc xóa email tuỳ theo checkbox
            if (rememberEmailCheckBox.isChecked()) {
                prefs.edit().putString("remembered_email", email).apply();
            } else {
                prefs.edit().remove("remembered_email").apply();
            }
            loginViewModel.login(email, password).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.getUser() != null) {
                    // Lưu userId, token vào SharedPreferences
                    prefs.edit()
                        .putLong("user_id", response.getUser().getId())
                        .putString("token", response.getToken())
                        .apply();
                    // Chuyển sang HomeFragment
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.homeFragment);
                } else {
                    CustomToast.showCustomToast(getContext(), "Đăng nhập thất bại", "Sai tài khoản hoặc mật khẩu!");
                }
            });
        });

        // Sự kiện chuyển sang giao diện đăng ký
        view.findViewById(R.id.registerTextView).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.registerFragment);
        });

        // Thêm TextWatcher để clear lỗi khi nhập lại
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

        return view;
    }
}
