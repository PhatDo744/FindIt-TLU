package com.example.findittlu.ui.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.findittlu.R;
import com.google.android.material.appbar.MaterialToolbar;

public class PolicyFragment extends Fragment {

    public PolicyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_policy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);

        // Ánh xạ các view
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);

        // Thiết lập Toolbar
        setupToolbar(toolbar, navController);
    }

    private void setupToolbar(MaterialToolbar toolbar, NavController navController) {
        // Sử dụng popBackStack() của NavController để quay lại màn hình trước đó
        toolbar.setNavigationOnClickListener(v -> {
            navController.popBackStack();
        });
    }
} 