package com.example.findittlu.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.findittlu.data.repository.PasswordResetRepository;
import com.example.findittlu.utils.VoidApiResponse;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PasswordResetViewModel extends ViewModel {

    private final PasswordResetRepository repository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // State for the whole flow
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> otp = new MutableLiveData<>();

    // LiveData for API call results
    private MutableLiveData<VoidApiResponse> forgotPasswordResult = new MutableLiveData<>();
    private MutableLiveData<VoidApiResponse> verifyOtpResult = new MutableLiveData<>();
    private MutableLiveData<VoidApiResponse> resetPasswordResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    // Rate limiting for OTP
    private final MutableLiveData<Boolean> canResendOtp = new MutableLiveData<>(true);
    private final MutableLiveData<Integer> resendCountdown = new MutableLiveData<>(0);
    private static final int RESEND_COOLDOWN_SECONDS = 60; // 1 phút
    private static final int MAX_RESEND_ATTEMPTS = 3; // Tối đa 3 lần gửi lại
    private int resendAttempts = 0;

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
    
    public LiveData<Boolean> getCanResendOtp() {
        return canResendOtp;
    }
    
    public LiveData<Integer> getResendCountdown() {
        return resendCountdown;
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
        
        // Kiểm tra rate limiting
        if (resendAttempts >= MAX_RESEND_ATTEMPTS) {
            forgotPasswordResult.setValue(new VoidApiResponse(VoidApiResponse.Status.ERROR, null, 
                "Bạn đã gửi OTP quá nhiều lần. Vui lòng thử lại sau 1 giờ."));
            return;
        }
        
        isLoading.setValue(true);
        resendAttempts++;
        
        repository.forgotPassword(email.getValue()).observeForever(apiResponse -> {
            forgotPasswordResult.setValue(apiResponse);
            isLoading.setValue(false);
            
            // Nếu thành công, bắt đầu countdown
            if (apiResponse.status == VoidApiResponse.Status.SUCCESS) {
                startResendCooldown();
            }
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
    
    private void startResendCooldown() {
        canResendOtp.setValue(false);
        resendCountdown.setValue(RESEND_COOLDOWN_SECONDS);
        
        scheduler.scheduleAtFixedRate(() -> {
            int currentCountdown = resendCountdown.getValue() != null ? resendCountdown.getValue() : 0;
            if (currentCountdown > 0) {
                resendCountdown.postValue(currentCountdown - 1);
            } else {
                canResendOtp.postValue(true);
                scheduler.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
    
    public void resetResendAttempts() {
        resendAttempts = 0;
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
    
    @Override
    protected void onCleared() {
        super.onCleared();
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
} 