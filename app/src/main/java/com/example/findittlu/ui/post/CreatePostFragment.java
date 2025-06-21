package com.example.findittlu.ui.post;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.findittlu.R;
import com.example.findittlu.viewmodel.CreatePostViewModel;
import com.example.findittlu.ui.post.adapter.SelectedImagesAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.content.Context;
import android.view.ContextThemeWrapper;

public class CreatePostFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int MAX_IMAGES = 3;

    private EditText dateEditText, titleEditText, descriptionEditText, locationEditText;
    private Spinner categorySpinner;
    private TextView dateLabelTextView;
    private MaterialButton lostItemButton, foundItemButton, submitButton;
    private boolean isFoundSelected = true;
    private CreatePostViewModel viewModel;
    private android.widget.CheckBox showEmailCheckBox, showPhoneCheckBox;
    
    // Image upload components
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private FrameLayout imageUploadContainer;
    private RecyclerView selectedImagesRecyclerView;
    private SelectedImagesAdapter selectedImagesAdapter;
    private final List<Object> imageItems = new ArrayList<>();

    public CreatePostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupImagePickerLauncher();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_post_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(this).get(CreatePostViewModel.class);

        initViews(view);
        setupToolbar(view, navController);
        setupDatePicker();
        setupCategorySpinner();
        setupPostTypeButtons();
        setupImageUpload();
        setupSubmitButton(navController);
        observeViewModel();
    }

    private void initViews(View view) {
        dateEditText = view.findViewById(R.id.dateEditText);
        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        locationEditText = view.findViewById(R.id.locationEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        dateLabelTextView = view.findViewById(R.id.dateLabelTextView);
        lostItemButton = view.findViewById(R.id.lostItemButton);
        foundItemButton = view.findViewById(R.id.foundItemButton);
        submitButton = view.findViewById(R.id.postButton);
        showEmailCheckBox = view.findViewById(R.id.showEmailCheckBox);
        showPhoneCheckBox = view.findViewById(R.id.showPhoneCheckBox);
        
        // Image upload views
        imageUploadContainer = view.findViewById(R.id.imageUploadContainer);
        selectedImagesRecyclerView = view.findViewById(R.id.selectedImagesRecyclerView);
    }

    private void setupImagePickerLauncher() {
        pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.getClipData() != null) { // User selected multiple images
                        int count = data.getClipData().getItemCount();
                        int availableSlots = MAX_IMAGES - imageItems.size();
                        int limit = Math.min(count, availableSlots);
                        
                        for (int i = 0; i < limit; i++) {
                            Uri imageUri = data.getClipData().getItemAt(i).getUri();
                            imageItems.add(imageUri);
                        }
                    } else if (data.getData() != null) { // User selected a single image
                        if (imageItems.size() < MAX_IMAGES) {
                            Uri imageUri = data.getData();
                            imageItems.add(imageUri);
                        }
                    }
                    
                    selectedImagesAdapter.notifyDataSetChanged();
                    updateImageUploadVisibility();
                }
            });
    }

    private void checkPermissionAndOpenGallery() {
        // Kiểm tra quyền truy cập ảnh
        boolean hasPermission = false;
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ sử dụng READ_MEDIA_IMAGES
            hasPermission = ContextCompat.checkSelfPermission(requireContext(), 
                Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            // Android < 13 sử dụng READ_EXTERNAL_STORAGE
            hasPermission = ContextCompat.checkSelfPermission(requireContext(), 
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        
        if (!hasPermission) {
            // Kiểm tra xem có nên hiển thị dialog giải thích không
            boolean shouldShowRationale = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), 
                    Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), 
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            
            if (shouldShowRationale) {
                // Hiển thị dialog giải thích
                showPermissionExplanationDialog();
            } else {
                // Xin quyền trực tiếp
                requestImagePermission();
            }
        } else {
            // Đã có quyền, mở gallery
            openImagePicker();
        }
    }
    
    private void showPermissionExplanationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Quyền truy cập ảnh")
            .setMessage("Ứng dụng cần quyền truy cập ảnh để bạn có thể chọn ảnh khi đăng tin. Chúng tôi chỉ truy cập ảnh khi bạn chủ động chọn.")
            .setPositiveButton("Đồng ý", (dialog, which) -> {
                requestImagePermission();
            })
            .setNegativeButton("Hủy", (dialog, which) -> {
                dialog.dismiss();
            })
            .show();
    }
    
    private void requestImagePermission() {
        String[] permissions;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        }
        ActivityCompat.requestPermissions(requireActivity(), permissions, PERMISSION_REQUEST_CODE);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền được cấp, mở gallery
                openImagePicker();
            } else {
                // Quyền bị từ chối
                boolean shouldShowRationale = false;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), 
                        Manifest.permission.READ_MEDIA_IMAGES);
                } else {
                    shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), 
                        Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                
                if (!shouldShowRationale) {
                    // Người dùng đã chọn "Không hỏi lại"
                    if (isAdded() && getActivity() != null) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), 
                            "Cần quyền truy cập ảnh để chọn ảnh. Vui lòng vào Cài đặt > Ứng dụng > FindIt@TLU > Quyền để cấp quyền.", 
                            Snackbar.LENGTH_LONG).show();
                    } else {
                        android.widget.Toast.makeText(getContext(), 
                            "Cần quyền truy cập ảnh để chọn ảnh. Vui lòng vào Cài đặt > Ứng dụng > FindIt@TLU > Quyền để cấp quyền.", 
                            android.widget.Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Người dùng từ chối nhưng có thể hỏi lại
                    if (isAdded() && getActivity() != null) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), 
                            "Cần quyền truy cập ảnh để chọn ảnh. Vui lòng cấp quyền để tiếp tục.", 
                            Snackbar.LENGTH_LONG).show();
                    } else {
                        android.widget.Toast.makeText(getContext(), 
                            "Cần quyền truy cập ảnh để chọn ảnh. Vui lòng cấp quyền để tiếp tục.", 
                            android.widget.Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
    
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Cho phép chọn nhiều ảnh
        intent.setType("image/*");
        // Chỉ chấp nhận ảnh
        String[] mimeTypes = {"image/jpeg", "image/png", "image/webp"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        
        pickImageLauncher.launch(intent);
    }
    
    private void updateImageUploadVisibility() {
        // Hiển thị RecyclerView nếu có ít nhất 1 ảnh được chọn
        selectedImagesRecyclerView.setVisibility(imageItems.isEmpty() ? View.GONE : View.VISIBLE);
        
        // Hiển thị nút upload nếu chưa đạt giới hạn
        imageUploadContainer.setVisibility(imageItems.size() >= MAX_IMAGES ? View.GONE : View.VISIBLE);
    }

    private void setupToolbar(View view, NavController navController) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            navController.popBackStack();
        });
    }

    private void setupDatePicker() {
        dateEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a ContextThemeWrapper to apply the custom theme
            final Context themedContext = new ContextThemeWrapper(requireContext(), R.style.MyDatePickerTheme);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    themedContext,
                    (dpView, year1, monthOfYear, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year1, monthOfYear, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dateEditText.setText(sdf.format(selectedDate.getTime()));
                    },
                    year, month, day);
            datePickerDialog.setOnShowListener(dialog -> {
                // Lấy màu từ resources
                int blueColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);

                // Đặt màu cho nút "OK" (Positive)
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(blueColor);

                // Đặt màu cho nút "Hủy" (Negative)
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(blueColor);
            });
            // Chỉ cho phép chọn ngày từ quá khứ đến hôm nay
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    private void setupCategorySpinner() {
        String[] categories = new String[]{"Chọn danh mục", "Đồ điện tử", "Giấy tờ", "Ví/Túi", "Quần áo", "Chìa khóa", "Khác"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), R.layout.spinner_item_white, categories) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
                } else {
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.text_primary));
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_white);
        categorySpinner.setAdapter(adapter);
    }

    private void setupPostTypeButtons() {
        updateButtonSelection();

        lostItemButton.setOnClickListener(v -> {
            if (isFoundSelected) {
                isFoundSelected = false;
                updateButtonSelection();
            }
        });

        foundItemButton.setOnClickListener(v -> {
            if (!isFoundSelected) {
                isFoundSelected = true;
                updateButtonSelection();
            }
        });
    }

    private void updateButtonSelection() {
        foundItemButton.setSelected(isFoundSelected);
        lostItemButton.setSelected(!isFoundSelected);
        updateHintsAndLabels();
    }

    private void updateHintsAndLabels() {
        if (!isFoundSelected) {
            dateLabelTextView.setText(getString(R.string.date_lost_label));
        } else {
            dateLabelTextView.setText(getString(R.string.date_found_label));
        }
    }

    private void setupSubmitButton(NavController navController) {
        submitButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String location = locationEditText.getText().toString().trim();
            int categoryId = categorySpinner.getSelectedItemPosition();
            String status = isFoundSelected ? "found" : "lost";
            
            // Validate contact info
            if (!showEmailCheckBox.isChecked() && !showPhoneCheckBox.isChecked()) {
                if (isAdded() && getActivity() != null) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Bạn phải chọn ít nhất một thông tin liên hệ để hiển thị.", Snackbar.LENGTH_LONG).show();
                } else {
                    android.widget.Toast.makeText(getContext(), "Bạn phải chọn ít nhất một thông tin liên hệ để hiển thị.", android.widget.Toast.LENGTH_LONG).show();
                }
                return; // Stop submission
            }

            // Disable the button to prevent multiple clicks
            submitButton.setEnabled(false);
            submitButton.setText("Đang đăng...");

            List<Uri> imageUris = imageItems.stream()
                    .filter(item -> item instanceof Uri)
                    .map(item -> (Uri) item)
                    .collect(Collectors.toList());

            viewModel.createPostWithImages(title, date, status, description, location, (int) categoryId, showEmailCheckBox.isChecked() || showPhoneCheckBox.isChecked(), imageUris);
        });
    }

    private void observeViewModel() {
        final NavController navController = isAdded() ? Navigation.findNavController(requireView()) : null;

        viewModel.getPostCreationState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            switch (state.status) {
                case LOADING:
                    submitButton.setEnabled(false);
                    submitButton.setText("Đang đăng...");
                    break;
                case SUCCESS:
                    submitButton.setEnabled(true);
                    submitButton.setText("Đăng tin");
                    if (isAdded() && getActivity() != null) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Bài viết của bạn đã được gửi để chờ duyệt!", Snackbar.LENGTH_LONG).show();
                    } else {
                        android.widget.Toast.makeText(getContext(), "Bài viết của bạn đã được gửi để chờ duyệt!", android.widget.Toast.LENGTH_LONG).show();
                    }
                    navController.popBackStack();
                    break;
                case ERROR:
                    submitButton.setEnabled(true);
                    submitButton.setText("Đăng tin");
                    if (isAdded() && getActivity() != null) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Lỗi: " + state.message, Snackbar.LENGTH_LONG).show();
                    } else {
                        android.widget.Toast.makeText(getContext(), "Lỗi: " + state.message, android.widget.Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        });
    }

    private void setupImageUpload() {
        // Setup RecyclerView for selected images
        selectedImagesAdapter = new SelectedImagesAdapter(imageItems, (imageItem, position) -> {
            imageItems.remove(position);
            selectedImagesAdapter.notifyItemRemoved(position);
            selectedImagesAdapter.notifyItemRangeChanged(position, imageItems.size());
            updateImageUploadVisibility();
        });
        
        selectedImagesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        selectedImagesRecyclerView.setAdapter(selectedImagesAdapter);
        
        // Setup image upload container click listener
        imageUploadContainer.setOnClickListener(v -> {
            if (imageItems.size() >= MAX_IMAGES) {
                if (isAdded() && getActivity() != null) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Bạn chỉ có thể chọn tối đa " + MAX_IMAGES + " ảnh", Snackbar.LENGTH_SHORT).show();
                } else {
                    android.widget.Toast.makeText(getContext(), "Bạn chỉ có thể chọn tối đa " + MAX_IMAGES + " ảnh", android.widget.Toast.LENGTH_SHORT).show();
                }
                return;
            }
            checkPermissionAndOpenGallery();
        });
        
        updateImageUploadVisibility();
    }
}
 