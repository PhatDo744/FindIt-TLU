package com.example.findittlu.ui.post;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextThemeWrapper;

import com.example.findittlu.R;
import com.example.findittlu.data.model.ItemImage;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.ui.post.adapter.SelectedImagesAdapter;
import com.example.findittlu.viewmodel.EditPostViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class EditPostFragment extends Fragment implements SelectedImagesAdapter.OnImageRemoveListener {

    private static final int MAX_IMAGES = 3;
    private EditPostViewModel viewModel;
    private EditText titleEditText, descriptionEditText, postTypeEditText, locationEditText, dateEditText, phoneEditText, emailEditText;
    private Spinner categorySpinner;
    private MaterialButton saveChangesButton, cancelButton;
    private String postId;
    private String[] categories;

    private FrameLayout imageUploadContainer;
    private RecyclerView selectedImagesRecyclerView;
    private SelectedImagesAdapter selectedImagesAdapter;
    private final List<Object> imageItems = new ArrayList<>();
    private final List<ItemImage> originalImages = new ArrayList<>();
    private ActivityResultLauncher<Intent> pickImageLauncher;

    public EditPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString("postId");
        }

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        if (result.getData().getClipData() != null) {
                            int count = result.getData().getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                if (imageItems.size() < MAX_IMAGES) {
                                    Uri imageUri = result.getData().getClipData().getItemAt(i).getUri();
                                    imageItems.add(imageUri);
                                }
                            }
                        } else if (result.getData().getData() != null) {
                            if (imageItems.size() < MAX_IMAGES) {
                                Uri imageUri = result.getData().getData();
                                imageItems.add(imageUri);
                            }
                        }
                        selectedImagesAdapter.notifyDataSetChanged();
                        updateImageUploadVisibility();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EditPostViewModel.class);
        final NavController navController = Navigation.findNavController(view);

        initViews(view);
        setupToolbar(view, navController);
        setupCategorySpinner();
        setupDatePicker();
        setupButtons(navController);
        
        setupImagePicker();
        observeViewModel();
        
        if (postId != null) {
            viewModel.fetchPostDetails(postId);
        }
    }

    private void initViews(View view) {
        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        postTypeEditText = view.findViewById(R.id.postTypeEditText);
        locationEditText = view.findViewById(R.id.locationEditText);
        dateEditText = view.findViewById(R.id.dateEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        saveChangesButton = view.findViewById(R.id.saveChangesButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        imageUploadContainer = view.findViewById(R.id.imageUploadContainer);
        selectedImagesRecyclerView = view.findViewById(R.id.selectedImagesRecyclerView);
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

    private void setupCategorySpinner() {
        // This should be populated from a remote source or a string array
        categories = new String[]{"Chọn danh mục", "Đồ điện tử", "Giấy tờ", "Ví/Túi", "Quần áo", "Chìa khóa", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_white, categories);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_white);
        categorySpinner.setAdapter(adapter);
    }
    
    private void setupImagePicker() {
        selectedImagesAdapter = new SelectedImagesAdapter(imageItems, this);
        selectedImagesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        selectedImagesRecyclerView.setAdapter(selectedImagesAdapter);

        imageUploadContainer.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            pickImageLauncher.launch(Intent.createChooser(intent, "Select Pictures"));
        });
        updateImageUploadVisibility();
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

    private void setupButtons(NavController navController) {
        saveChangesButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            long categoryId = categorySpinner.getSelectedItemPosition(); // This is the position, not the real ID
            String location = locationEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty() || categoryId == 0 || location.isEmpty() || date.isEmpty()) {
                if (isAdded() && getActivity() != null) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Vui lòng điền đầy đủ thông tin", Snackbar.LENGTH_SHORT).show();
                } else {
                    android.widget.Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", android.widget.Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // So sánh để xác định ảnh cần xóa và ảnh cần upload
            List<ItemImage> imagesToDelete = new ArrayList<>();
            for (ItemImage img : originalImages) {
                if (!imageItems.contains(img)) {
                    imagesToDelete.add(img);
                }
            }
            List<Uri> imagesToUpload = imageItems.stream()
                    .filter(item -> item instanceof Uri)
                    .map(item -> (Uri) item)
                    .collect(Collectors.toList());

            // Thực hiện xóa ảnh trước, sau đó upload ảnh mới, cuối cùng mới cập nhật bài đăng
            deleteImagesAndContinue(imagesToDelete, imagesToUpload, title, description, categoryId, location, date, navController);
        });

        cancelButton.setOnClickListener(v -> {
            // Khôi phục lại danh sách ảnh như ban đầu
            imageItems.clear();
            imageItems.addAll(originalImages);
            selectedImagesAdapter.notifyDataSetChanged();
            updateImageUploadVisibility();
            navController.popBackStack();
        });
    }

    // Xóa ảnh tuần tự, sau đó upload ảnh mới, cuối cùng cập nhật bài đăng
    private void deleteImagesAndContinue(List<ItemImage> imagesToDelete, List<Uri> imagesToUpload, String title, String description, long categoryId, String location, String date, NavController navController) {
        if (imagesToDelete.isEmpty()) {
            uploadImagesAndContinue(imagesToUpload, title, description, categoryId, location, date, navController);
            return;
        }
        ItemImage img = imagesToDelete.remove(0);
        viewModel.deleteImage(img);
        viewModel.getImageDeleteResult().observe(getViewLifecycleOwner(), result -> {
            if (result.getSuccess()) {
                imageItems.remove(result.getDeletedItem());
                selectedImagesAdapter.notifyDataSetChanged();
                updateImageUploadVisibility();
                // Tiếp tục xóa ảnh tiếp theo
                deleteImagesAndContinue(imagesToDelete, imagesToUpload, title, description, categoryId, location, date, navController);
            } else {
                if (isAdded() && getActivity() != null) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Xóa ảnh thất bại: " + result.getError(), Snackbar.LENGTH_SHORT).show();
                } else {
                    android.widget.Toast.makeText(getContext(), "Xóa ảnh thất bại: " + result.getError(), android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImagesAndContinue(List<Uri> imagesToUpload, String title, String description, long categoryId, String location, String date, NavController navController) {
        if (imagesToUpload.isEmpty()) {
            // Không có ảnh mới, cập nhật bài đăng
            viewModel.savePostChanges(postId, title, description, categoryId, location, date);
            return;
        }
        viewModel.uploadNewImages(postId, imagesToUpload);
        viewModel.getImageUploadResult().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                // Sau khi upload xong ảnh mới, cập nhật bài đăng
                viewModel.savePostChanges(postId, title, description, categoryId, location, date);
            } else {
                if (isAdded() && getActivity() != null) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Tải ảnh mới lên thất bại.", Snackbar.LENGTH_LONG).show();
                } else {
                    android.widget.Toast.makeText(getContext(), "Tải ảnh mới lên thất bại.", android.widget.Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
    private void observeViewModel() {
        viewModel.getPostDetails().observe(getViewLifecycleOwner(), this::populatePostData);
        viewModel.getUpdateResult().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                // Now, upload new images
                List<Uri> newImageUris = imageItems.stream()
                        .filter(item -> item instanceof Uri)
                        .map(item -> (Uri) item)
                        .collect(Collectors.toList());

                if (!newImageUris.isEmpty()) {
                    viewModel.uploadNewImages(postId, newImageUris);
                } else {
                    // If no new images, we're done
                    if (isAdded() && getActivity() != null) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Cập nhật thành công! Bài viết của bạn đã được gửi để duyệt lại.", Snackbar.LENGTH_LONG).show();
                    } else {
                        android.widget.Toast.makeText(getContext(), "Cập nhật thành công! Bài viết của bạn đã được gửi để duyệt lại.", android.widget.Toast.LENGTH_LONG).show();
                    }
                    if (isAdded()) {
                        Navigation.findNavController(requireView()).popBackStack();
                    }
                }

            } else {
                if (isAdded() && getActivity() != null) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Cập nhật thông tin thất bại.", Snackbar.LENGTH_SHORT).show();
                } else {
                    android.widget.Toast.makeText(getContext(), "Cập nhật thông tin thất bại.", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getImageUploadResult().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                if (isAdded() && getActivity() != null) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Cập nhật thành công! Bài viết của bạn đã được gửi để duyệt lại.", Snackbar.LENGTH_LONG).show();
                } else {
                    android.widget.Toast.makeText(getContext(), "Cập nhật thành công! Bài viết của bạn đã được gửi để duyệt lại.", android.widget.Toast.LENGTH_LONG).show();
                }
                if (isAdded()) {
                    Navigation.findNavController(requireView()).popBackStack();
                }
            } else {
                if (isAdded() && getActivity() != null) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Cập nhật thông tin thành công, nhưng tải ảnh lên thất bại.", Snackbar.LENGTH_LONG).show();
                } else {
                    android.widget.Toast.makeText(getContext(), "Cập nhật thông tin thành công, nhưng tải ảnh lên thất bại.", android.widget.Toast.LENGTH_LONG).show();
                }
            }
        });

        viewModel.getImageDeleteResult().observe(getViewLifecycleOwner(), result -> {
            if (result.getSuccess()) {
                if (isAdded() && getActivity() != null) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Đã xóa ảnh.", Snackbar.LENGTH_SHORT).show();
                } else {
                    android.widget.Toast.makeText(getContext(), "Đã xóa ảnh.", android.widget.Toast.LENGTH_SHORT).show();
                }
                imageItems.remove(result.getDeletedItem());
                selectedImagesAdapter.notifyDataSetChanged();
                updateImageUploadVisibility();
            } else {
                if (isAdded() && getActivity() != null) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Xóa ảnh thất bại: " + result.getError(), Snackbar.LENGTH_SHORT).show();
                } else {
                    android.widget.Toast.makeText(getContext(), "Xóa ảnh thất bại: " + result.getError(), android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void populatePostData(Post post) {
        if (post == null) return;
        
        titleEditText.setText(post.getTitle());
        descriptionEditText.setText(post.getDescription());
        locationEditText.setText(post.getLocationDescription());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (post.getDateLostOrFound() != null) {
            dateEditText.setText(sdf.format(post.getDateLostOrFound()));
        }
        
        if ("found".equalsIgnoreCase(post.getItemType())) {
            postTypeEditText.setText("Nhặt được");
        } else {
            postTypeEditText.setText("Bị mất");
        }
        
        // This is a simple mapping. A real app should use a CategoryAdapter.
        if (post.getCategoryId() > 0 && post.getCategoryId() < categories.length) {
            categorySpinner.setSelection((int)post.getCategoryId());
        }
        
        // TODO: Populate contact info if available and public
        if (post.isContactInfoPublic() && post.getUser() != null) {
            phoneEditText.setText(post.getUser().getPhoneNumber());
            emailEditText.setText(post.getUser().getEmail());
        }

        // Populate images
        imageItems.clear();
        originalImages.clear();
        if (post.getImages() != null) {
            imageItems.addAll(post.getImages());
            originalImages.addAll(post.getImages());
        }
        selectedImagesAdapter.notifyDataSetChanged();
        updateImageUploadVisibility();
    }

    @Override
    public void onImageRemove(Object imageItem, int position) {
        // Chỉ xóa khỏi danh sách tạm, không gọi API
        imageItems.remove(position);
        selectedImagesAdapter.notifyItemRemoved(position);
        selectedImagesAdapter.notifyItemRangeChanged(position, imageItems.size());
        updateImageUploadVisibility();
    }

    private void updateImageUploadVisibility() {
        if (imageItems.size() >= MAX_IMAGES) {
            imageUploadContainer.setVisibility(View.GONE);
        } else {
            imageUploadContainer.setVisibility(View.VISIBLE);
        }
    }
} 