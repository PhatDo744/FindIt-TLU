package com.example.findittlu.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.lifecycle.ViewModelProvider;

import com.example.findittlu.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.example.findittlu.viewmodel.UserViewModel;
import com.example.findittlu.data.model.User;
import com.example.findittlu.data.repository.UserRepository;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.findittlu.utils.ImageUtils;
import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.widget.TextView;
import com.example.findittlu.utils.CustomToast;
import android.text.TextWatcher;
import android.text.Editable;

public class EditProfileFragment extends Fragment {

    private ImageView avatarImageView;
    private ImageView cameraIcon;
    private TextView changeAvatarTextView;
    private ActivityResultLauncher<Intent> pickAvatarLauncher;
    private UserViewModel userViewModel;
    private Uri selectedAvatarUri;
    private boolean hasAvatarChanged = false; // Flag để kiểm tra ảnh có thay đổi không
    private String originalAvatarUrl; // Lưu URL ảnh gốc
    
    // TextInputLayout references
    private TextInputLayout fullNameInputLayout;
    private TextInputLayout phoneInputLayout;
    private EditText fullNameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        // Thiết lập Toolbar
        setupToolbar(view, navController);

        // Khởi tạo view và ViewModel
        avatarImageView = view.findViewById(R.id.avatarImageView);
        cameraIcon = view.findViewById(R.id.cameraIcon);
        changeAvatarTextView = view.findViewById(R.id.changeAvatarTextView);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Khởi tạo TextInputLayout và EditText
        fullNameInputLayout = view.findViewById(R.id.fullNameInputLayout);
        phoneInputLayout = view.findViewById(R.id.phoneInputLayout);
        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        emailEditText = view.findViewById(R.id.emailEditText);

        // Đăng ký ActivityResultLauncher để chọn ảnh
        pickAvatarLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        selectedAvatarUri = uri;
                        hasAvatarChanged = true; // Đánh dấu ảnh đã thay đổi
                        avatarImageView.setImageURI(uri); // Chỉ hiển thị preview, không upload
                        CustomToast.showCustomToast(getContext(), "Thông báo", "Ảnh đã được chọn. Nhấn 'Lưu' để cập nhật!");
                    }
                }
            });

        // Bắt sự kiện click vào icon bút chì/camera và text đổi avatar
        cameraIcon.setOnClickListener(v -> openImagePicker());
        changeAvatarTextView.setOnClickListener(v -> openImagePicker());

        // Lấy dữ liệu user và bind lên UI
        userViewModel.getProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                fullNameEditText.setText(user.getFullName());
                phoneEditText.setText(user.getPhoneNumber());
                emailEditText.setText(user.getEmail());
                originalAvatarUrl = user.getPhotoUrl(); // Lưu URL ảnh gốc
                if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                    ImageUtils.loadAvatar(requireContext(), user.getPhotoUrl(), avatarImageView);
                } else {
                    avatarImageView.setImageResource(R.drawable.ic_person);
                }
            }
        });

        // Thiết lập TextWatcher để clear lỗi khi user nhập
        setupTextWatchers();

        // Thiết lập các nút
        setupButtons(view, navController);
    }

    private void setupTextWatchers() {
        // TextWatcher cho fullNameEditText
        fullNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fullNameInputLayout.setError(null);
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

    private void setupToolbar(View view, NavController navController) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            navController.popBackStack();
        });
    }

    private void setupButtons(View view, NavController navController) {
        MaterialButton saveButton = view.findViewById(R.id.saveButton);
        MaterialButton cancelButton = view.findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(v -> {
            String fullName = fullNameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();

            // Clear tất cả lỗi trước khi validate
            clearAllErrors();

            // Kiểm tra dữ liệu đầu vào
            boolean hasError = false;

            if (fullName.isEmpty()) {
                fullNameInputLayout.setError("Vui lòng nhập họ tên!");
                hasError = true;
            } else if (fullName.length() > 255) {
                fullNameInputLayout.setError("Họ tên không được vượt quá 255 ký tự!");
                hasError = true;
            } else if (!isValidFullName(fullName)) {
                fullNameInputLayout.setError("Họ tên chỉ được chứa chữ cái, dấu tiếng Việt và khoảng trắng!");
                hasError = true;
            }

            if (phone.isEmpty()) {
                phoneInputLayout.setError("Vui lòng nhập số điện thoại!");
                hasError = true;
            } else if (!isValidVietnamesePhoneNumber(phone)) {
                phoneInputLayout.setError("Số điện thoại không đúng định dạng. Vui lòng nhập số điện thoại Việt Nam hợp lệ (VD: 0987654321, 0912345678, +84987654321)!");
                hasError = true;
            }

            if (hasError) {
                return;
            }

            // Disable nút để tránh click nhiều lần
            saveButton.setEnabled(false);
            saveButton.setText("Đang lưu...");

            // Nếu có ảnh mới được chọn, upload ảnh trước
            if (hasAvatarChanged && selectedAvatarUri != null) {
                userViewModel.uploadAvatar(requireContext(), selectedAvatarUri).observe(getViewLifecycleOwner(), user -> {
                    // Luôn tiếp tục cập nhật thông tin, bất kể kết quả upload ảnh
                    // Vì API có thể đã upload thành công ngay cả khi trả về null
                    updateUserProfile(fullName, phone, email, navController, saveButton);
                });
            } else {
                // Không có ảnh mới, chỉ cập nhật thông tin
                updateUserProfile(fullName, phone, email, navController, saveButton);
            }
        });

        cancelButton.setOnClickListener(v -> {
            // Nếu có ảnh đã thay đổi, khôi phục lại ảnh cũ
            if (hasAvatarChanged && originalAvatarUrl != null) {
                ImageUtils.loadAvatar(requireContext(), originalAvatarUrl, avatarImageView);
            }
            navController.popBackStack();
        });
    }

    private void clearAllErrors() {
        fullNameInputLayout.setError(null);
        phoneInputLayout.setError(null);
    }

    private void updateUserProfile(String fullName, String phone, String email, NavController navController, MaterialButton saveButton) {
        User user = new User();
        user.setFullName(fullName);
        user.setPhoneNumber(phone);
        user.setEmail(email);

        userViewModel.updateProfile(user).observe(getViewLifecycleOwner(), apiResponse -> {
            saveButton.setEnabled(true);
            saveButton.setText("Lưu thay đổi");
            
            if (apiResponse.isSuccess()) {
                String message = "Cập nhật thông tin thành công!";
                if (hasAvatarChanged) {
                    message += " (bao gồm ảnh đại diện)";
                }
                CustomToast.showCustomToast(getContext(), "Thành công", message);
                navController.popBackStack();
            } else {
                // Hiển thị lỗi từ server trên các trường tương ứng
                String errorMessage = apiResponse.getErrorMessage();
                if (errorMessage.contains("họ tên")) {
                    fullNameInputLayout.setError(errorMessage);
                } else if (errorMessage.contains("số điện thoại") || errorMessage.contains("phone_number")) {
                    phoneInputLayout.setError(errorMessage);
                } else {
                    // Nếu không xác định được trường cụ thể, hiển thị toast
                    CustomToast.showCustomToast(getContext(), "Thất bại", errorMessage);
                }
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickAvatarLauncher.launch(Intent.createChooser(intent, "Chọn ảnh đại diện"));
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

    private boolean isValidFullName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return false;
        }

        // Kiểm tra không chứa số
        if (fullName.matches(".*\\d.*")) {
            return false;
        }

        // Kiểm tra chỉ chứa chữ cái, dấu tiếng Việt và khoảng trắng
        // Pattern này bao gồm:
        // - Chữ cái tiếng Anh: a-z, A-Z
        // - Chữ cái tiếng Việt có dấu: À-ỹ (Unicode range cho tiếng Việt)
        // - Khoảng trắng: \s
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
} 