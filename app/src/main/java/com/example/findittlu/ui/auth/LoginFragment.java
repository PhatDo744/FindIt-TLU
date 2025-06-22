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
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.findittlu.R;
import com.example.findittlu.viewmodel.LoginViewModel;
import com.example.findittlu.utils.CustomToast;
import com.google.android.material.textfield.TextInputLayout;
import android.text.TextWatcher;
import android.text.Editable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoginFragment extends Fragment {
    private LoginViewModel loginViewModel;
    private NavController navController;
    private SharedPreferences prefs;
    private ScrollView leftScrollView;
    private ScrollView rightScrollView;
    
    // View bindings
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private EditText emailEditText;
    private EditText passwordEditText;
    private CheckBox rememberEmailCheckBox;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        // Check if already logged in
        String token = prefs.getString("token", null);
        if (token != null && !token.isEmpty()) {
            navController.navigate(R.id.action_loginFragment_to_homeFragment);
            return; // Stop further execution
        }

        // Initialize views
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        rememberEmailCheckBox = view.findViewById(R.id.rememberEmailCheckBox);
        emailInputLayout = view.findViewById(R.id.emailInputLayout);
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout);

        // Restore remembered email
        String savedEmail = prefs.getString("remembered_email", "");
        if (!savedEmail.isEmpty()) {
            emailEditText.setText(savedEmail);
            rememberEmailCheckBox.setChecked(true);
        }

        // Xử lý đồng bộ cuộn cho layout landscape
        leftScrollView = view.findViewById(R.id.leftScrollView);
        rightScrollView = view.findViewById(R.id.rightScrollView);
        
        if (leftScrollView != null && rightScrollView != null) {
            setupScrollSync();
        }

        setupTextWatchers();
        setupClickListeners(view);
    }

    private void setupTextWatchers() {
        TextWatcher errorClearingWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (emailInputLayout.getError() != null) emailInputLayout.setError(null);
                if (passwordInputLayout.getError() != null) passwordInputLayout.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        emailEditText.addTextChangedListener(errorClearingWatcher);
        passwordEditText.addTextChangedListener(errorClearingWatcher);
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.loginButton).setOnClickListener(v -> handleLogin());
        view.findViewById(R.id.registerTextView).setOnClickListener(v ->
                navController.navigate(R.id.action_loginFragment_to_registerFragment));
        view.findViewById(R.id.forgotPasswordTextView).setOnClickListener(v ->
                navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment));
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInput(email, password)) {
            return;
        }

        // Save or remove remembered email
        prefs.edit().putBoolean("remember_email_checked", rememberEmailCheckBox.isChecked()).apply();
        if (rememberEmailCheckBox.isChecked()) {
            prefs.edit().putString("remembered_email", email).apply();
        } else {
            prefs.edit().remove("remembered_email").apply();
        }

        loginViewModel.login(email, password).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getUser() != null) {
                // Save user session
                prefs.edit()
                        .putLong("user_id", response.getUser().getId())
                        .putString("token", response.getToken())
                        .putString("user_email", response.getUser().getEmail())
                        .apply();
                // Navigate to Home
                navController.navigate(R.id.action_loginFragment_to_homeFragment);
            } else {
                CustomToast.showCustomToast(getContext(), "Đăng nhập thất bại", "Sai tài khoản hoặc mật khẩu!");
            }
        });
    }

    private boolean validateInput(String email, String password) {
        clearAllErrors();
        boolean hasError = false;

        if (email.isEmpty()) {
            emailInputLayout.setError("Vui lòng nhập email!");
            hasError = true;
        } else if (!isValidEmail(email)) {
            emailInputLayout.setError("Email không đúng định dạng!");
            hasError = true;
        }

        if (password.isEmpty()) {
            passwordInputLayout.setError("Vui lòng nhập mật khẩu!");
            hasError = true;
        } else if (password.length() < 8) {
            passwordInputLayout.setError("Mật khẩu phải có ít nhất 8 ký tự!");
            hasError = true;
        }
        return !hasError;
    }

    private void clearAllErrors() {
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        // Kiểm tra định dạng email cơ bản
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }

    private void setupScrollSync() {
        // Chỉ cho phép cuộn từ cột phải, cột trái sẽ theo dõi
        leftScrollView.setOnTouchListener((v, event) -> {
            // Vô hiệu hóa touch events trên cột trái
            return true;
        });

        // Đồng bộ cuộn từ phải sang trái
        rightScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            leftScrollView.scrollTo(0, rightScrollView.getScrollY());
        });
    }
}
