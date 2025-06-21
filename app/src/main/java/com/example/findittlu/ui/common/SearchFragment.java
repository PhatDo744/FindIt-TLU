package com.example.findittlu.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.findittlu.R;
import com.example.findittlu.adapter.SearchResultAdapter;
import com.example.findittlu.adapter.SearchResultItem;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import android.content.res.ColorStateList;
import androidx.lifecycle.ViewModelProvider;

import com.example.findittlu.viewmodel.CategoryViewModel;
import com.example.findittlu.data.model.Category;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.findittlu.utils.CustomToast;

public class SearchFragment extends Fragment {
    private RecyclerView searchResultRecyclerView;
    private SearchResultAdapter searchResultAdapter;
    private List<SearchResultItem> searchResultList;
    private SearchViewModel searchViewModel;
    private CategoryViewModel categoryViewModel;
    private LinearLayout categoryLayout;
    private java.util.Map<String, Long> categoryNameToId = new java.util.HashMap<>();
    private String selectedItemType = null; // "lost", "found" hoặc null
    private Long selectedCategoryId = null;

    public SearchFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        searchResultRecyclerView = view.findViewById(R.id.searchResultRecyclerView);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultList = new ArrayList<>();
        searchResultAdapter = new SearchResultAdapter(new ArrayList<>());
        searchResultRecyclerView.setAdapter(searchResultAdapter);
        categoryLayout = view.findViewById(R.id.categoryLayout);
        setupFilterButtons(view);
        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            setupCategoryButtonsDynamic(view, categories);
        });
        searchViewModel.getIsApiConnected().observe(getViewLifecycleOwner(), connected -> {
            if (!connected) CustomToast.showCustomToast(getContext(), "Lỗi kết nối", "Không thể kết nối API!");
        });
        searchViewModel.getSearchResults().observe(getViewLifecycleOwner(), list -> {
            searchResultAdapter = new SearchResultAdapter(list);
            searchResultRecyclerView.setAdapter(searchResultAdapter);
            // Cập nhật số kết quả
            TextView tvResultCount = view.findViewById(R.id.tvResultCount);
            tvResultCount.setText("Tìm thấy " + list.size() + " kết quả");
            // Ẩn/hiện nút xem thêm
            MaterialButton btnLoadMore = view.findViewById(R.id.btnLoadMore);
            if (list.size() < 5 || searchViewModel.isShowAllResults()) {
                btnLoadMore.setVisibility(View.GONE);
            } else {
                btnLoadMore.setVisibility(View.VISIBLE);
            }
        });
        searchViewModel.fetchSearchResults("");

        EditText searchEditText = view.findViewById(R.id.searchEditText);
        ImageButton searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            String keyword = searchEditText.getText().toString().trim();
            if (!keyword.isEmpty()) {
                searchViewModel.fetchSearchResultsByKeyword(keyword);
            }
        });

        // Sự kiện bấm 'Xem thêm kết quả'
        view.findViewById(R.id.btnLoadMore).setOnClickListener(v -> {
            searchViewModel.setShowAllResults(true);
        });
    }

    private void setupFilterButtons(View view) {
        LinearLayout statusLayout = (LinearLayout) ((ViewGroup) view.findViewById(R.id.statusLayout));
        statusLayout.removeAllViews();
        Context themedContext = new android.view.ContextThemeWrapper(requireContext(), R.style.CategoryButton);
        String[] statusTexts = {"Tất cả", "Đã mất", "Đã tìm thấy"};
        MaterialButton[] statusButtons = new MaterialButton[statusTexts.length];
        for (int i = 0; i < statusTexts.length; i++) {
            MaterialButton btn = new MaterialButton(themedContext, null, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMarginEnd((int) getResources().getDimension(R.dimen.category_button_margin_end));
            btn.setLayoutParams(params);
            btn.setText(statusTexts[i]);
            btn.setAllCaps(false);
            statusLayout.addView(btn);
            statusButtons[i] = btn;
        }
        // Gán sự kiện click
        statusButtons[0].setOnClickListener(v -> {
            selectCategoryButton(statusButtons[0], statusLayout);
            selectedItemType = null;
            triggerFilter();
        });
        statusButtons[1].setOnClickListener(v -> {
            selectCategoryButton(statusButtons[1], statusLayout);
            selectedItemType = "lost";
            triggerFilter();
        });
        statusButtons[2].setOnClickListener(v -> {
            selectCategoryButton(statusButtons[2], statusLayout);
            selectedItemType = "found";
            triggerFilter();
        });
        // Mặc định chọn "Tất cả"
        selectCategoryButton(statusButtons[0], statusLayout);
    }

    private void selectButton(MaterialButton selected, MaterialButton[] group) {
        int blue = requireContext().getResources().getColor(R.color.primary_blue);
        int white = requireContext().getResources().getColor(R.color.white);
        int gray = requireContext().getResources().getColor(R.color.text_secondary);
        for (MaterialButton btn : group) {
            if (btn == selected) {
                btn.setBackgroundTintList(ColorStateList.valueOf(blue));
                btn.setTextColor(white);
            } else {
                btn.setBackgroundTintList(ColorStateList.valueOf(white));
                btn.setTextColor(gray);
            }
        }
    }

    private void setupCategoryButtonsDynamic(View view, java.util.List<Category> categories) {
        LinearLayout layout = (LinearLayout) ((ViewGroup) view.findViewById(R.id.categoryLayout));
        layout.removeAllViews();
        categoryNameToId.clear();
        Context themedContext = new android.view.ContextThemeWrapper(requireContext(), R.style.CategoryButton);
        com.google.android.material.button.MaterialButton btnAll = new com.google.android.material.button.MaterialButton(themedContext, null, 0);
        LinearLayout.LayoutParams paramsAll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsAll.setMarginEnd((int) getResources().getDimension(R.dimen.category_button_margin_end));
        btnAll.setLayoutParams(paramsAll);
        btnAll.setText("Tất cả");
        btnAll.setAllCaps(false);
        btnAll.setOnClickListener(v -> {
            selectCategoryButton(btnAll, layout);
            selectedCategoryId = null;
            triggerFilter();
        });
        layout.addView(btnAll);
        if (categories != null) {
            for (Category cat : categories) {
                categoryNameToId.put(cat.getName(), cat.getId());
                com.google.android.material.button.MaterialButton btn = new com.google.android.material.button.MaterialButton(themedContext, null, 0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMarginEnd((int) getResources().getDimension(R.dimen.category_button_margin_end));
                btn.setLayoutParams(params);
                btn.setText(cat.getName());
                btn.setAllCaps(false);
                btn.setOnClickListener(v -> {
                    selectCategoryButton(btn, layout);
                    selectedCategoryId = cat.getId();
                    triggerFilter();
                });
                layout.addView(btn);
            }
        }
        selectCategoryButton(btnAll, layout);
    }

    private void selectCategoryButton(com.google.android.material.button.MaterialButton selectedBtn, LinearLayout layout) {
        int count = layout.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = layout.getChildAt(i);
            if (v instanceof com.google.android.material.button.MaterialButton) {
                com.google.android.material.button.MaterialButton btn = (com.google.android.material.button.MaterialButton) v;
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(android.R.color.transparent)));
                btn.setTextColor(getResources().getColor(R.color.text_secondary));
            }
        }
        selectedBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary_blue)));
        selectedBtn.setTextColor(getResources().getColor(R.color.white));
    }

    private void triggerFilter() {
        if (selectedCategoryId != null && selectedItemType != null) {
            searchViewModel.fetchSearchResultsByCategoryAndType(selectedCategoryId, selectedItemType);
        } else if (selectedCategoryId != null) {
            searchViewModel.fetchSearchResultsByCategory(selectedCategoryId);
        } else if (selectedItemType != null) {
            searchViewModel.fetchSearchResultsByType(selectedItemType);
        } else {
            searchViewModel.fetchSearchResults("");
        }
    }
}