package com.example.findittlu;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.findittlu.data.api.RetrofitClient;
import com.example.findittlu.utils.KeyboardUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Khởi tạo RetrofitClient với context
        RetrofitClient.init(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNavigationView, navController);

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.loginFragment || destination.getId() == R.id.registerFragment) {
                    bottomNavigationView.setVisibility(View.GONE);
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            });
        }

        KeyboardUtils.addKeyboardVisibilityListener(this, isVisible -> {
            if (isVisible) {
                bottomNavigationView.setVisibility(View.GONE);
            } else {
                // Chỉ hiện lại nếu không phải màn hình login/register
                NavHostFragment hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                if (hostFragment != null) {
                    NavController navController = hostFragment.getNavController();
                    int currentDestinationId = navController.getCurrentDestination().getId();
                    if (currentDestinationId != R.id.loginFragment && currentDestinationId != R.id.registerFragment) {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
} 