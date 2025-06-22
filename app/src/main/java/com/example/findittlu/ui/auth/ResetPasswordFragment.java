package com.example.findittlu.ui.auth;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.findittlu.R;
import com.example.findittlu.databinding.FragmentResetPasswordBinding;
import com.example.findittlu.utils.CustomToast;
import com.example.findittlu.utils.VoidApiResponse;
import com.example.findittlu.viewmodel.PasswordResetViewModel;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPasswordFragment extends Fragment {

    private FragmentResetPasswordBinding binding;
    private PasswordResetViewModel viewModel;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(PasswordResetViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        setListeners();
        observeViewModel();
    }

    private void setListeners() {
        binding.resetPasswordButton.setOnClickListener(v -> handleResetPassword());
        binding.backButton.setOnClickListener(v -> navController.navigateUp());
        
        // Clear errors when user starts typing
        binding.passwordEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.passwordInputLayout.getError() != null) {
                    binding.passwordInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
        
        binding.confirmPasswordEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.confirmPasswordInputLayout.getError() != null) {
                    binding.confirmPasswordInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void observeViewModel() {
        viewModel.getResetPasswordResult().observe(getViewLifecycleOwner(), this::handleApiResponse);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::handleLoading);
    }

    private void handleResetPassword() {
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();

        if (validatePassword(binding.passwordInputLayout, password) &&
            validateConfirmPassword(binding.confirmPasswordInputLayout, password, confirmPassword)) {
            viewModel.resetPassword(password, confirmPassword);
        }
    }

    private boolean validatePassword(TextInputLayout layout, String password) {
        if (password.isEmpty()) {
            layout.setError("Mật khẩu không được để trống");
            return false;
        }
        if (password.length() < 8) {
            layout.setError("Mật khẩu phải có ít nhất 8 ký tự");
            return false;
        }
        
        // Kiểm tra độ mạnh mật khẩu
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isLowerCase(c)) hasLowerCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecialChar = true;
        }
        
        if (!hasUpperCase || !hasLowerCase || !hasDigit) {
            layout.setError("Mật khẩu phải chứa chữ hoa, chữ thường và số");
            return false;
        }
        
        layout.setError(null);
        return true;
    }

    private boolean validateConfirmPassword(TextInputLayout layout, String password, String confirmPassword) {
        if (confirmPassword.isEmpty()) {
            layout.setError("Vui lòng xác nhận mật khẩu");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            layout.setError("Mật khẩu xác nhận không khớp");
            return false;
        }
        layout.setError(null);
        return true;
    }

    private void handleApiResponse(VoidApiResponse apiResponse) {
        if (apiResponse == null) return;

        switch (apiResponse.status) {
            case SUCCESS:
                CustomToast.showCustomToast(requireContext(), "Thành công", "Đặt lại mật khẩu thành công!");
                viewModel.clearResetPasswordResult();
                // Pop back to login screen
                navController.popBackStack(R.id.loginFragment, false);
                break;
            case ERROR:
                // Hiển thị lỗi cụ thể từ server
                String errorMessage = apiResponse.errorMessage != null ? apiResponse.errorMessage : "Không thể đặt lại mật khẩu";
                CustomToast.showCustomToast(requireContext(), "Lỗi", errorMessage, true);
                
                // Nếu lỗi liên quan đến mật khẩu, hiển thị trên input
                if (errorMessage.toLowerCase().contains("password") || 
                    errorMessage.toLowerCase().contains("mật khẩu")) {
                    binding.passwordInputLayout.setError(errorMessage);
                } else if (errorMessage.toLowerCase().contains("token") || 
                          errorMessage.toLowerCase().contains("otp")) {
                    // Nếu OTP hết hạn, quay về màn hình verify OTP
                    CustomToast.showCustomToast(requireContext(), "Lỗi", "OTP đã hết hạn. Vui lòng thử lại.", true);
                    navController.navigateUp();
                }
                viewModel.clearResetPasswordResult();
                break;
            case FAILURE:
                CustomToast.showCustomToast(requireContext(), "Lỗi hệ thống", "Vui lòng thử lại sau.", true);
                viewModel.clearResetPasswordResult();
                break;
        }
    }

    private void handleLoading(Boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.resetPasswordButton.setEnabled(!isLoading);
        binding.resetPasswordButton.setText(isLoading ? "Đang xử lý..." : "Đặt lại mật khẩu");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 