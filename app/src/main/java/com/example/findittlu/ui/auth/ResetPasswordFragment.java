package com.example.findittlu.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    }

    private void observeViewModel() {
        viewModel.getResetPasswordResult().observe(getViewLifecycleOwner(), this::handleApiResponse);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::handleLoading);
    }

    private void handleResetPassword() {
        String password = binding.passwordEditText.getText().toString();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString();

        boolean isPasswordValid = validatePassword(binding.passwordInputLayout, password);
        boolean isConfirmPasswordValid = validateConfirmPassword(binding.confirmPasswordInputLayout, password, confirmPassword);

        if (isPasswordValid && isConfirmPasswordValid) {
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
                CustomToast.showCustomToast(requireContext(), "Lỗi", "Không thể đặt lại mật khẩu. " + apiResponse.errorMessage, true);
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 