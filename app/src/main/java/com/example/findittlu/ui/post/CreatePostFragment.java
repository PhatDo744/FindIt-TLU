package com.example.findittlu.ui.post;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.findittlu.R;
import com.example.findittlu.viewmodel.CreatePostViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreatePostFragment extends Fragment {

    private EditText dateEditText, titleEditText, descriptionEditText, locationEditText;
    private Spinner categorySpinner;
    private TextView dateLabelTextView;
    private MaterialButton lostItemButton, foundItemButton, submitButton;
    private boolean isFoundSelected = true;
    private CreatePostViewModel viewModel;
    private android.widget.CheckBox showEmailCheckBox, showPhoneCheckBox;

    public CreatePostFragment() {
        // Required empty public constructor
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
    }

    private void setupToolbar(View view, NavController navController) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
    }

    private void setupDatePicker() {
        dateEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (dpView, year1, monthOfYear, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year1, monthOfYear, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dateEditText.setText(sdf.format(selectedDate.getTime()));
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupCategorySpinner() {
        String[] categories = new String[]{"Chọn danh mục", "Đồ điện tử", "Giấy tờ", "Ví/Túi", "Quần áo", "Chìa khóa", "Khác"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, categories) {
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

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
            String status = isFoundSelected ? "FOUND" : "SEARCHING";
            boolean isContactInfoPublic = showEmailCheckBox.isChecked() || showPhoneCheckBox.isChecked();
            if (title.isEmpty() || date.isEmpty() || categoryId == 0 || description.isEmpty() || location.isEmpty()) {
                Snackbar.make(v, "Vui lòng điền đầy đủ thông tin", Snackbar.LENGTH_SHORT).show();
                return;
            }
            viewModel.createPost(title, date, status, description, location, categoryId, isContactInfoPublic);
        });
    }

    private void observeViewModel() {
        viewModel.getCreationResult().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Snackbar.make(requireView(), "Đăng bài thành công!", Snackbar.LENGTH_SHORT).show();
                // Pop back to the previous screen
                Navigation.findNavController(requireView()).popBackStack();
            } else {
                Snackbar.make(requireView(), "Đã xảy ra lỗi. Vui lòng thử lại.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
 