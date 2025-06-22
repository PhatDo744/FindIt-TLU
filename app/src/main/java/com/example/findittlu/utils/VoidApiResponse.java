package com.example.findittlu.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Response;

public class VoidApiResponse {
    public final Status status;

    @Nullable
    public final Throwable error;

    @Nullable
    public final String errorMessage;


    public VoidApiResponse(Status status, @Nullable Throwable error, @Nullable String errorMessage) {
        this.status = status;
        this.error = error;
        this.errorMessage = errorMessage;
    }

    public static VoidApiResponse success() {
        return new VoidApiResponse(Status.SUCCESS, null, null);
    }

    public static VoidApiResponse error(Response<?> response) {
        String msg = "Đã xảy ra lỗi";
        if (response.errorBody() != null) {
            try {
                String errorBody = response.errorBody().string();
                // Thử parse JSON error response
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(errorBody, JsonObject.class);
                
                if (jsonObject.has("message")) {
                    msg = jsonObject.get("message").getAsString();
                } else if (jsonObject.has("error")) {
                    msg = jsonObject.get("error").getAsString();
                } else if (jsonObject.has("errors")) {
                    // Xử lý validation errors
                    JsonObject errors = jsonObject.getAsJsonObject("errors");
                    if (errors.has("email")) {
                        msg = errors.getAsJsonArray("email").get(0).getAsString();
                    } else if (errors.has("token")) {
                        msg = errors.getAsJsonArray("token").get(0).getAsString();
                    } else if (errors.has("password")) {
                        msg = errors.getAsJsonArray("password").get(0).getAsString();
                    } else {
                        // Lấy error đầu tiên
                        String firstError = errors.entrySet().iterator().next().getValue()
                                .getAsJsonArray().get(0).getAsString();
                        msg = firstError;
                    }
                } else {
                    msg = errorBody;
                }
            } catch (Exception e) {
                // Nếu không parse được JSON, sử dụng error body trực tiếp
                try {
                    msg = response.errorBody().string();
                } catch (Exception ex) {
                    msg = "Đã xảy ra lỗi (Mã: " + response.code() + ")";
                }
            }
        } else {
            msg = "Đã xảy ra lỗi (Mã: " + response.code() + ")";
        }
        return new VoidApiResponse(Status.ERROR, null, msg);
    }

    public static VoidApiResponse failure(@NonNull Throwable error) {
        String errorMsg = "Kết nối mạng không ổn định";
        if (error.getMessage() != null && !error.getMessage().isEmpty()) {
            errorMsg = error.getMessage();
        }
        return new VoidApiResponse(Status.FAILURE, error, errorMsg);
    }

    public enum Status {
        SUCCESS,
        ERROR,
        FAILURE
    }
} 