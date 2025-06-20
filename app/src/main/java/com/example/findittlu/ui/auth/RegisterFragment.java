package com.example.findittlu.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.findittlu.R;
import com.example.findittlu.viewmodel.RegisterViewModel;
import com.example.findittlu.data.model.LoginResponse;

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

        view.findViewById(R.id.registerButton).setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }
            registerViewModel.register(name, email, password, confirmPassword, phone).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.getUser() != null) {
                    Toast.makeText(getContext(), "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
                    androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(requireActivity(), com.example.findittlu.R.id.nav_host_fragment);
                    navController.navigate(com.example.findittlu.R.id.loginFragment);
                } else {
                    Toast.makeText(getContext(), "Đăng ký thất bại! Kiểm tra lại thông tin.", Toast.LENGTH_SHORT).show();
                }
            });
        });
        return view;
    }
}
