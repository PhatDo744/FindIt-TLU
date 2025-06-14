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

public class SearchFragment extends Fragment {
    private RecyclerView searchResultRecyclerView;
    private SearchResultAdapter searchResultAdapter;
    private List<SearchResultItem> searchResultList;
    private SearchViewModel searchViewModel;

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
        searchResultRecyclerView = view.findViewById(R.id.searchResultRecyclerView);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultList = new ArrayList<>();
        searchResultList.add(new SearchResultItem(R.drawable.image_placeholder_background, true, "Ví da màu đen hiệu Gucci", "Ví/Túi xách", "Thư viện T45", "15/05/2025"));
        searchResultList.add(new SearchResultItem(R.drawable.image_placeholder_background, false, "Thẻ sinh viên Hoàng Tiến Phúc", "Đồ điện tử", "Sân bóng TLU", "16/05/2025"));
        searchResultList.add(new SearchResultItem(R.drawable.image_placeholder_background, true, "Máy tính laptop", "Đồ điện tử", "Kí túc xá K1", "16/05/2025"));
        searchResultAdapter = new SearchResultAdapter(new ArrayList<>());
        searchResultRecyclerView.setAdapter(searchResultAdapter);
        setupFilterButtons(view);
        searchViewModel.getIsApiConnected().observe(getViewLifecycleOwner(), connected -> {
            if (!connected) Toast.makeText(getContext(), "Không thể kết nối API!", Toast.LENGTH_SHORT).show();
        });
        searchViewModel.getSearchResults().observe(getViewLifecycleOwner(), list -> {
            searchResultAdapter = new SearchResultAdapter(list);
            searchResultRecyclerView.setAdapter(searchResultAdapter);
        });
        searchViewModel.fetchSearchResults("");
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
        selectButton(btnCategoryAll, categoryButtons);
        btnStatusAll.setOnClickListener(v -> selectButton(btnStatusAll, statusButtons));
        btnStatusLost.setOnClickListener(v -> selectButton(btnStatusLost, statusButtons));
        btnStatusFound.setOnClickListener(v -> selectButton(btnStatusFound, statusButtons));
        btnCategoryAll.setOnClickListener(v -> selectButton(btnCategoryAll, categoryButtons));
        btnCategoryElectronics.setOnClickListener(v -> selectButton(btnCategoryElectronics, categoryButtons));
        btnCategoryPaper.setOnClickListener(v -> selectButton(btnCategoryPaper, categoryButtons));
        btnCategoryWallet.setOnClickListener(v -> selectButton(btnCategoryWallet, categoryButtons));
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
} 