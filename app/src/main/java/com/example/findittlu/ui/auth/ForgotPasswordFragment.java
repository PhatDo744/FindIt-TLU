package com.example.findittlu.ui.auth;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.findittlu.R;
import com.example.findittlu.databinding.FragmentForgotPasswordBinding;
import com.example.findittlu.utils.CustomToast;
import com.example.findittlu.utils.VoidApiResponse;
import com.example.findittlu.viewmodel.PasswordResetViewModel;
import com.google.android.material.textfield.TextInputLayout;


public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding binding;
    private PasswordResetViewModel viewModel;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
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
        binding.sendButton.setOnClickListener(v -> handleSendRequest());
        binding.backButton.setOnClickListener(v -> navController.navigateUp());
        binding.loginTextView.setOnClickListener(v -> navController.navigate(R.id.action_forgotPasswordFragment_to_loginFragment));
    }

    private void observeViewModel() {
        viewModel.getForgotPasswordResult().observe(getViewLifecycleOwner(), this::handleApiResponse);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::handleLoading);
    }

    private void handleSendRequest() {
        String email = binding.emailEditText.getText().toString().trim();
        if (validateInput(binding.emailInputLayout, email)) {
            viewModel.setEmail(email);
            viewModel.forgotPassword();
        }
    }

    private boolean validateInput(TextInputLayout layout, String email) {
        if (email.isEmpty()) {
            layout.setError("Email không được để trống");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layout.setError("Email không hợp lệ");
            return false;
        }
        if (!email.endsWith("tlu.edu.vn")) {
            layout.setError("Email phải là email của TLU");
            return false;
        }
        layout.setError(null);
        return true;
    }

    private void handleApiResponse(VoidApiResponse apiResponse) {
        if (apiResponse == null) return;

        switch (apiResponse.status) {
            case SUCCESS:
                CustomToast.showCustomToast(requireContext(), "Thành công", "Đã gửi OTP đến email của bạn");
                viewModel.clearForgotPasswordResult();
                navController.navigate(R.id.action_forgotPasswordFragment_to_verifyOtpFragment);
                break;
            case ERROR:
                CustomToast.showCustomToast(requireContext(), "Lỗi", "Không thể gửi OTP. " + apiResponse.errorMessage, true);
                viewModel.clearForgotPasswordResult();
                break;
            case FAILURE:
                CustomToast.showCustomToast(requireContext(), "Lỗi hệ thống", "Vui lòng thử lại sau.", true);
                viewModel.clearForgotPasswordResult();
                break;
        }
    }

    private void handleLoading(Boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.sendButton.setEnabled(!isLoading);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 