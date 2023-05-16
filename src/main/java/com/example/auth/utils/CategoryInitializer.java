package com.example.auth.utils;

import com.example.auth.data.entity.Category;
import com.example.auth.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CategoryInitializer implements CommandLineRunner {
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {

        Resource filePath = new ClassPathResource("initials/Categories.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(Objects.requireNonNull(filePath.getFile())))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length == 3) {
                    Category category = new Category(data[0], data[1], data[2]);
                    categoryRepository.save(category);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}