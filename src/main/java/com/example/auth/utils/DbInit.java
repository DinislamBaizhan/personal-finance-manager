package com.example.auth.utils;

import com.example.auth.data.entity.Category;
import com.example.auth.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbInit {
    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void postConstruct() {
        for (long i = 0; i < 10; i++) {
            Category category = new Category();
            category.setId(i);
            category.setColor("Sas " + i);
            category.setIcon("asdasd" + i);
            categoryRepository.save(category);
        }
    }
}
