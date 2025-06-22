package com.example.findittlu.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.findittlu.data.repository.PasswordResetRepository;
import com.example.findittlu.utils.VoidApiResponse;

public class PasswordResetViewModel extends ViewModel {

    private final PasswordResetRepository repository;

    // State for the whole flow
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> otp = new MutableLiveData<>();

    // LiveData for API call results
    private MutableLiveData<VoidApiResponse> forgotPasswordResult = new MutableLiveData<>();
    private MutableLiveData<VoidApiResponse> verifyOtpResult = new MutableLiveData<>();
    private MutableLiveData<VoidApiResponse> resetPasswordResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);


    public PasswordResetViewModel() {
        repository = new PasswordResetRepository();
    }

    // --- Getters for results ---
    public LiveData<VoidApiResponse> getForgotPasswordResult() {
        return forgotPasswordResult;
    }

    public LiveData<VoidApiResponse> getVerifyOtpResult() {
        return verifyOtpResult;
    }

    public LiveData<VoidApiResponse> getResetPasswordResult() {
        return resetPasswordResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }


    // --- Setters for state ---
    public void setEmail(String email) {
        this.email.setValue(email);
    }

    public String getEmail() {
        return email.getValue();
    }
    
    public void setOtp(String otp) {
        this.otp.setValue(otp);
    }

    public String getOtp() {
        return otp.getValue();
    }


    // --- API Calls ---
    public void forgotPassword() {
        if (email.getValue() == null || email.getValue().isEmpty()) return;
        isLoading.setValue(true);
        repository.forgotPassword(email.getValue()).observeForever(apiResponse -> {
            forgotPasswordResult.setValue(apiResponse);
            isLoading.setValue(false);
        });
    }

    public void verifyOtp() {
        if (email.getValue() == null || otp.getValue() == null) return;
        isLoading.setValue(true);
        repository.verifyOtp(email.getValue(), otp.getValue()).observeForever(apiResponse -> {
            verifyOtpResult.setValue(apiResponse);
            isLoading.setValue(false);
        });
    }

    public void resetPassword(String password, String passwordConfirmation) {
        if (email.getValue() == null || otp.getValue() == null) return;
        isLoading.setValue(true);
        repository.resetPassword(email.getValue(), otp.getValue(), password, passwordConfirmation)
                .observeForever(apiResponse -> {
                    resetPasswordResult.setValue(apiResponse);
                    isLoading.setValue(false);
                });
    }

    public void clearForgotPasswordResult() {
        forgotPasswordResult = new MutableLiveData<>();
    }

    public void clearVerifyOtpResult() {
        verifyOtpResult = new MutableLiveData<>();
    }
    
    public void clearResetPasswordResult() {
        resetPasswordResult = new MutableLiveData<>();
    }
} 