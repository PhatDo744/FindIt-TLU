package com.example.findittlu.ui.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.findittlu.R;

public class LoginFragment extends Fragment {
    private LoginViewModel loginViewModel;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);

        // Kiểm tra nếu đã đăng nhập thì chuyển sang HomeFragment2
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        if (userId != -1) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.homeFragment2);
            return view;
        }

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        EditText emailEditText = view.findViewById(R.id.emailEditText);
        CheckBox rememberEmailCheckBox = view.findViewById(R.id.rememberEmailCheckBox);

        // Lấy email đã lưu (nếu có)
        String savedEmail = prefs.getString("remembered_email", "");
        if (!savedEmail.isEmpty()) {
            emailEditText.setText(savedEmail);
            rememberEmailCheckBox.setChecked(true);
        }

        // Xử lý nút đăng nhập
        view.findViewById(R.id.loginButton).setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = ((EditText) view.findViewById(R.id.passwordEditText)).getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ email và mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }
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
                    // Chuyển sang HomeFragment2
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.homeFragment2);
                } else {
                    Toast.makeText(getContext(), "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
}
