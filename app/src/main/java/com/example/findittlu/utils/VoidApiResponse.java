package com.example.findittlu.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import retrofit2.Response;

public class VoidApiResponse {
    public final Status status;

    @Nullable
    public final Throwable error;

    @Nullable
    public final String errorMessage;


    private VoidApiResponse(Status status, @Nullable Throwable error, @Nullable String errorMessage) {
        this.status = status;
        this.error = error;
        this.errorMessage = errorMessage;
    }

    public static VoidApiResponse success() {
        return new VoidApiResponse(Status.SUCCESS, null, null);
    }

    public static VoidApiResponse error(Response<?> response) {
        String msg = "An error occurred";
        if (response.errorBody() != null) {
            try {
                // TODO: Parse the error body for a more specific message
                msg = response.errorBody().string();
            } catch (Exception e) {
                // Ignore
            }
        }
        return new VoidApiResponse(Status.ERROR, null, msg);
    }

    public static VoidApiResponse failure(@NonNull Throwable error) {
        return new VoidApiResponse(Status.FAILURE, error, error.getMessage());
    }

    public enum Status {
        SUCCESS,
        ERROR,
        FAILURE
    }
} 