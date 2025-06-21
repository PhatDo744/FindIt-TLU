package com.example.findittlu.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.findittlu.data.model.Category;
import com.example.findittlu.data.repository.CategoryRepository;
import java.util.List;

public class CategoryViewModel extends ViewModel {
    private final CategoryRepository categoryRepository = new CategoryRepository();
    public LiveData<List<Category>> getCategories() {
        return categoryRepository.getCategories();
    }
} 