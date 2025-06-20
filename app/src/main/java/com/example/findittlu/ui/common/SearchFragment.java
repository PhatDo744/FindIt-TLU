package com.example.findittlu.ui.common;

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
import android.widget.Toast;
import com.example.findittlu.viewmodel.CategoryViewModel;
import com.example.findittlu.data.model.Category;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.ImageButton;

public class SearchFragment extends Fragment {
    private RecyclerView searchResultRecyclerView;
    private SearchResultAdapter searchResultAdapter;
    private List<SearchResultItem> searchResultList;
    private SearchViewModel searchViewModel;
    private CategoryViewModel categoryViewModel;
    private LinearLayout categoryLayout;
    private java.util.Map<String, Long> categoryNameToId = new java.util.HashMap<>();
    private Long selectedCategoryId = null;

    public SearchFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        searchResultRecyclerView = view.findViewById(R.id.searchResultRecyclerView);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultList = new ArrayList<>();
        searchResultList.add(new SearchResultItem(R.drawable.image_placeholder_background, true, "Ví da màu đen hiệu Gucci", "Ví/Túi xách", "Thư viện T45", "15/05/2025"));
        searchResultList.add(new SearchResultItem(R.drawable.image_placeholder_background, false, "Thẻ sinh viên Hoàng Tiến Phúc", "Đồ điện tử", "Sân bóng TLU", "16/05/2025"));
        searchResultList.add(new SearchResultItem(R.drawable.image_placeholder_background, true, "Máy tính laptop", "Đồ điện tử", "Kí túc xá K1", "16/05/2025"));
        searchResultAdapter = new SearchResultAdapter(new ArrayList<>());
        searchResultRecyclerView.setAdapter(searchResultAdapter);
        categoryLayout = view.findViewById(R.id.categoryLayout);
        setupFilterButtons(view);
        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            setupCategoryButtonsDynamic(view, categories);
        });
        searchViewModel.getIsApiConnected().observe(getViewLifecycleOwner(), connected -> {
            if (!connected) Toast.makeText(getContext(), "Không thể kết nối API!", Toast.LENGTH_SHORT).show();
        });
        searchViewModel.getSearchResults().observe(getViewLifecycleOwner(), list -> {
            searchResultAdapter = new SearchResultAdapter(list);
            searchResultRecyclerView.setAdapter(searchResultAdapter);
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
    }

    private void setupFilterButtons(View view) {
        MaterialButton btnStatusAll = view.findViewById(R.id.btnStatusAll);
        MaterialButton btnStatusLost = view.findViewById(R.id.btnStatusLost);
        MaterialButton btnStatusFound = view.findViewById(R.id.btnStatusFound);
        MaterialButton[] statusButtons = {btnStatusAll, btnStatusLost, btnStatusFound};
        MaterialButton btnCategoryAll = view.findViewById(R.id.btnCategoryAll);
        MaterialButton btnCategoryElectronics = view.findViewById(R.id.btnCategoryElectronics);
        MaterialButton btnCategoryPaper = view.findViewById(R.id.btnCategoryPaper);
        MaterialButton btnCategoryWallet = view.findViewById(R.id.btnCategoryWallet);
        MaterialButton[] categoryButtons = {btnCategoryAll, btnCategoryElectronics, btnCategoryPaper, btnCategoryWallet};
        selectButton(btnStatusAll, statusButtons);
        btnStatusAll.setOnClickListener(v -> {
            selectButton(btnStatusAll, statusButtons);
            searchViewModel.fetchSearchResults("");
        });
        btnStatusLost.setOnClickListener(v -> {
            selectButton(btnStatusLost, statusButtons);
            searchViewModel.fetchSearchResultsByType("lost");
        });
        btnStatusFound.setOnClickListener(v -> {
            selectButton(btnStatusFound, statusButtons);
            searchViewModel.fetchSearchResultsByType("found");
        });
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
        com.google.android.material.button.MaterialButton btnAll = new com.google.android.material.button.MaterialButton(requireContext(), null, R.style.CategoryButton);
        LinearLayout.LayoutParams paramsAll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsAll.setMarginEnd(16);
        btnAll.setLayoutParams(paramsAll);
        btnAll.setText("Tất cả");
        btnAll.setOnClickListener(v -> {
            selectCategoryButton(btnAll, layout);
            selectedCategoryId = null;
            searchViewModel.fetchSearchResults("");
        });
        layout.addView(btnAll);
        if (categories != null) {
            for (Category cat : categories) {
                categoryNameToId.put(cat.getName(), cat.getId());
                com.google.android.material.button.MaterialButton btn = new com.google.android.material.button.MaterialButton(requireContext(), null, R.style.CategoryButton);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMarginEnd(16);
                btn.setLayoutParams(params);
                btn.setText(cat.getName());
                btn.setOnClickListener(v -> {
                    selectCategoryButton(btn, layout);
                    selectedCategoryId = cat.getId();
                    searchViewModel.fetchSearchResultsByCategory(selectedCategoryId);
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
}