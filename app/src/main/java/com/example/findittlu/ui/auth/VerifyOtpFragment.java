package com.example.findittlu.ui.auth;

import android.os.Bundle;
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
import com.example.findittlu.databinding.FragmentVerifyOtpBinding;
import com.example.findittlu.utils.CustomToast;
import com.example.findittlu.utils.VoidApiResponse;
import com.example.findittlu.viewmodel.PasswordResetViewModel;

public class VerifyOtpFragment extends Fragment {

    private FragmentVerifyOtpBinding binding;
    private PasswordResetViewModel viewModel;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVerifyOtpBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(PasswordResetViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        binding.emailDisplayTextView.setText(viewModel.getEmail());

        setListeners();
        observeViewModel();
    }

    private void setListeners() {
        binding.verifyButton.setOnClickListener(v -> handleVerifyOtp());
        binding.resendOtpTextView.setOnClickListener(v -> resendOtp());
        binding.backButton.setOnClickListener(v -> navController.navigateUp());
    }

    private void observeViewModel() {
        viewModel.getVerifyOtpResult().observe(getViewLifecycleOwner(), this::handleVerifyApiResponse);
        viewModel.getForgotPasswordResult().observe(getViewLifecycleOwner(), this::handleResendApiResponse);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::handleLoading);
        viewModel.getCanResendOtp().observe(getViewLifecycleOwner(), this::handleCanResendOtp);
        viewModel.getResendCountdown().observe(getViewLifecycleOwner(), this::handleResendCountdown);
    }

    private void handleVerifyOtp() {
        String otp = binding.otpEditText.getText().toString().trim();
        if (otp.length() != 6) {
            binding.otpInputLayout.setError("Mã OTP phải có 6 chữ số");
            return;
        }
        binding.otpInputLayout.setError(null);
        viewModel.setOtp(otp);
        viewModel.verifyOtp();
    }

    private void resendOtp() {
        if (viewModel.getCanResendOtp().getValue() != null && viewModel.getCanResendOtp().getValue()) {
            CustomToast.showCustomToast(requireContext(), "Thông báo", "Đang gửi lại OTP...");
            viewModel.forgotPassword(); // Re-use the forgotPassword method to send a new OTP
        }
    }

    private void handleVerifyApiResponse(VoidApiResponse apiResponse) {
        if (apiResponse == null) return;

        switch (apiResponse.status) {
            case SUCCESS:
                CustomToast.showCustomToast(requireContext(), "Thành công", "Xác thực OTP thành công!");
                viewModel.clearVerifyOtpResult();
                navController.navigate(R.id.action_verifyOtpFragment_to_resetPasswordFragment);
                break;
            case ERROR:
                binding.otpInputLayout.setError("OTP không chính xác hoặc đã hết hạn");
                CustomToast.showCustomToast(requireContext(), "Lỗi", apiResponse.errorMessage, true);
                viewModel.clearVerifyOtpResult();
                break;
            case FAILURE:
                CustomToast.showCustomToast(requireContext(), "Lỗi hệ thống", "Vui lòng thử lại sau", true);
                viewModel.clearVerifyOtpResult();
                break;
        }
    }

    private void handleResendApiResponse(VoidApiResponse apiResponse) {
        if (apiResponse == null) return;

        switch (apiResponse.status) {
            case SUCCESS:
                CustomToast.showCustomToast(requireContext(), "Thành công", "Đã gửi lại OTP đến email của bạn");
                viewModel.clearForgotPasswordResult();
                break;
            case ERROR:
                CustomToast.showCustomToast(requireContext(), "Lỗi", "Không thể gửi lại OTP. " + apiResponse.errorMessage, true);
                viewModel.clearForgotPasswordResult();
                break;
            case FAILURE:
                CustomToast.showCustomToast(requireContext(), "Lỗi hệ thống", "Vui lòng thử lại sau", true);
                viewModel.clearForgotPasswordResult();
                break;
        }
    }

    private void handleLoading(Boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.verifyButton.setEnabled(!isLoading);
        binding.resendOtpTextView.setEnabled(!isLoading);
    }
    
    private void handleCanResendOtp(Boolean canResend) {
        if (canResend != null) {
            binding.resendOtpTextView.setEnabled(canResend);
            if (canResend) {
                binding.resendOtpTextView.setText("Gửi lại OTP");
                binding.resendOtpTextView.setTextColor(getResources().getColor(R.color.primary_blue));
            }
        }
    }
    
    private void handleResendCountdown(Integer countdown) {
        if (countdown != null && countdown > 0) {
            binding.resendOtpTextView.setText("Gửi lại OTP (" + countdown + "s)");
            binding.resendOtpTextView.setTextColor(getResources().getColor(R.color.text_secondary));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 