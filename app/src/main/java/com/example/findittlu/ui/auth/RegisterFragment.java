package com.example.findittlu.ui.auth;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.findittlu.R;
import com.example.findittlu.viewmodel.RegisterViewModel;
import com.example.findittlu.data.model.LoginResponse;
import com.example.findittlu.utils.CustomToast;
import com.google.android.material.textfield.TextInputLayout;
import android.text.TextWatcher;
import android.text.Editable;

public class RegisterFragment extends Fragment {
    private RegisterViewModel registerViewModel;
    
    // TextInputLayout references
    private TextInputLayout nameInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout confirmPasswordInputLayout;
    private TextInputLayout phoneInputLayout;
    
    // EditText references
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText phoneEditText;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        
        // Sự kiện chuyển sang giao diện đăng nhập
        view.findViewById(R.id.loginTextView).setOnClickListener(v -> {
            androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(requireActivity(), com.example.findittlu.R.id.nav_host_fragment);
            navController.navigate(com.example.findittlu.R.id.loginFragment);
        });

        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Khởi tạo views
        nameEditText = view.findViewById(R.id.fullNameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);

        nameInputLayout = view.findViewById(R.id.nameInputLayout);
        emailInputLayout = view.findViewById(R.id.emailInputLayout);
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = view.findViewById(R.id.confirmPasswordInputLayout);
        phoneInputLayout = view.findViewById(R.id.phoneInputLayout);

        // Thiết lập TextWatcher để clear lỗi khi user nhập
        setupTextWatchers();

        view.findViewById(R.id.registerButton).setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            
            // Clear tất cả lỗi trước khi validate
            clearAllErrors();
            
            // Kiểm tra dữ liệu đầu vào
            boolean hasError = false;

            if (name.isEmpty()) {
                nameInputLayout.setError("Vui lòng nhập họ tên!");
                hasError = true;
            } else if (name.length() > 255) {
                nameInputLayout.setError("Họ tên không được vượt quá 255 ký tự!");
                hasError = true;
            } else if (!isValidFullName(name)) {
                nameInputLayout.setError("Họ tên chỉ được chứa chữ cái, dấu tiếng Việt và khoảng trắng!");
                hasError = true;
            }

            if (email.isEmpty()) {
                emailInputLayout.setError("Vui lòng nhập email!");
                hasError = true;
            } else if (!isValidEmail(email)) {
                emailInputLayout.setError("Email không đúng định dạng!");
                hasError = true;
            } else if (!isValidTLUEmail(email)) {
                emailInputLayout.setError("Email phải có đuôi @tlu.edu.vn hoặc @e.tlu.edu.vn!");
                hasError = true;
            }

            if (password.isEmpty()) {
                passwordInputLayout.setError("Vui lòng nhập mật khẩu!");
                hasError = true;
            } else if (password.length() < 8) {
                passwordInputLayout.setError("Mật khẩu phải có ít nhất 8 ký tự!");
                hasError = true;
            } else if (!isValidPassword(password)) {
                passwordInputLayout.setError("Mật khẩu phải có ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt!");
                hasError = true;
            }

            if (confirmPassword.isEmpty()) {
                confirmPasswordInputLayout.setError("Vui lòng xác nhận mật khẩu!");
                hasError = true;
            }

            if (phone.isEmpty()) {
                phoneInputLayout.setError("Vui lòng nhập số điện thoại!");
                hasError = true;
            } else if (!isValidVietnamesePhoneNumber(phone)) {
                phoneInputLayout.setError("Số điện thoại không đúng định dạng. Vui lòng nhập số điện thoại Việt Nam hợp lệ!");
                hasError = true;
            }

            if (hasError) return;
            
            if (!password.equals(confirmPassword)) {
                confirmPasswordInputLayout.setError("Mật khẩu xác nhận không khớp!");
                return;
            }

            registerViewModel.register(name, email, password, confirmPassword, phone).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.getUser() != null) {
                    CustomToast.showCustomToast(getContext(), "Đăng ký thành công", "Vui lòng đăng nhập.");
                    androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(requireActivity(), com.example.findittlu.R.id.nav_host_fragment);
                    navController.navigate(com.example.findittlu.R.id.loginFragment);
                } else {
                    CustomToast.showCustomToast(getContext(), "Đăng ký thất bại", "Kiểm tra lại thông tin.");
                }
            });
        });

        return view;
    }

    private void setupTextWatchers() {
        // TextWatcher cho nameEditText
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // TextWatcher cho emailEditText
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // TextWatcher cho passwordEditText
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordInputLayout.setError(null);
                // Clear confirm password error when password changes
                confirmPasswordInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // TextWatcher cho confirmPasswordEditText
        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmPasswordInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // TextWatcher cho phoneEditText
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void clearAllErrors() {
        nameInputLayout.setError(null);
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);
        confirmPasswordInputLayout.setError(null);
        phoneInputLayout.setError(null);
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        // Kiểm tra định dạng email cơ bản
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }

    private boolean isValidFullName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return false;
        }

        // Kiểm tra không chứa số
        if (fullName.matches(".*\\d.*")) {
            return false;
        }

        // Kiểm tra chỉ chứa chữ cái, dấu tiếng Việt và khoảng trắng
        if (!fullName.matches("^[a-zA-ZÀ-ỹ\\s]+$")) {
            return false;
        }

        // Kiểm tra không có khoảng trắng liên tiếp
        if (fullName.matches(".*\\s{2,}.*")) {
            return false;
        }

        // Kiểm tra không bắt đầu hoặc kết thúc bằng khoảng trắng
        if (!fullName.equals(fullName.trim())) {
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        // Kiểm tra có ít nhất 1 chữ hoa
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        // Kiểm tra có ít nhất 1 chữ thường
        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        // Kiểm tra có ít nhất 1 số
        if (!password.matches(".*\\d.*")) {
            return false;
        }

        // Kiểm tra có ít nhất 1 ký tự đặc biệt
        if (!password.matches(".*[@$!%*?&].*")) {
            return false;
        }

        return true;
    }

    private boolean isValidVietnamesePhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }

        // Loại bỏ khoảng trắng và ký tự đặc biệt
        String cleanPhone = phone.replaceAll("[^0-9+]", "");
        
        // Định nghĩa các đầu số hợp lệ của các nhà mạng Việt Nam
        String[] validPrefixes = {
            // Viettel
            "03", "05", "07", "08", "09",
            // MobiFone
            "07", "08", "09",
            // Vinaphone
            "03", "05", "08", "09",
            // Vietnamobile
            "05", "08",
            // Gmobile
            "05", "08",
            // Itelecom
            "08"
        };
        
        // Kiểm tra format số điện thoại Việt Nam
        String[] patterns = {
            "^0[3-9][0-9]{8}$", // Format nội địa: 0xx xxxx xxx
            "^\\+84[3-9][0-9]{8}$", // Format quốc tế: +84xx xxxx xxx
            "^84[3-9][0-9]{8}$" // Format quốc tế: 84xx xxxx xxx
        };
        
        for (String pattern : patterns) {
            if (cleanPhone.matches(pattern)) {
                // Kiểm tra thêm đầu số có hợp lệ không
                if (cleanPhone.startsWith("0")) {
                    String prefix = cleanPhone.substring(0, 2);
                    for (String validPrefix : validPrefixes) {
                        if (validPrefix.equals(prefix)) {
                            return true;
                        }
                    }
                } else if (cleanPhone.startsWith("+84")) {
                    String prefix = cleanPhone.substring(3, 5);
                    for (String validPrefix : validPrefixes) {
                        if (validPrefix.equals(prefix)) {
                            return true;
                        }
                    }
                } else if (cleanPhone.startsWith("84")) {
                    String prefix = cleanPhone.substring(2, 4);
                    for (String validPrefix : validPrefixes) {
                        if (validPrefix.equals(prefix)) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }

    private boolean isValidTLUEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        // Kiểm tra định dạng email cơ bản
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern) && (email.endsWith("@tlu.edu.vn") || email.endsWith("@e.tlu.edu.vn"));
    }
}
