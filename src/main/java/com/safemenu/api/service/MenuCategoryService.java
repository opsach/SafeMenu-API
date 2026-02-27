package com.safemenu.api.service;

import com.safemenu.api.dto.request.CategoryRequest;
import com.safemenu.api.dto.response.CategoryResponse;
import com.safemenu.api.entity.MenuCategory;
import com.safemenu.api.entity.Restaurant;
import com.safemenu.api.exception.ResourceNotFoundException;
import com.safemenu.api.mapper.EntityMapper;
import com.safemenu.api.repository.MenuCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuCategoryService {

    private final MenuCategoryRepository categoryRepository;
    private final RestaurantService restaurantService;
    private final EntityMapper mapper;

    public List<CategoryResponse> findByRestaurant(Long restaurantId) {
        return categoryRepository.findByRestaurantIdOrderByDisplayOrderAsc(restaurantId).stream()
                .map(mapper::toCategoryResponse)
                .toList();
    }

    public CategoryResponse findById(Long id) {
        return mapper.toCategoryResponse(getEntityById(id));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Restaurant restaurant = restaurantService.getEntityById(request.getRestaurantId());

        MenuCategory category = MenuCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder())
                .restaurant(restaurant)
                .build();

        return mapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        MenuCategory category = getEntityById(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder());

        return mapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        MenuCategory category = getEntityById(id);
        categoryRepository.delete(category);
    }

    public MenuCategory getEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuCategory", id));
    }
}
