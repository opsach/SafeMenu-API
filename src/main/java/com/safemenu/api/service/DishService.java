package com.safemenu.api.service;

import com.safemenu.api.dto.request.DishRequest;
import com.safemenu.api.dto.response.DishResponse;
import com.safemenu.api.entity.Dish;
import com.safemenu.api.entity.Ingredient;
import com.safemenu.api.entity.MenuCategory;
import com.safemenu.api.enums.AllergenType;
import com.safemenu.api.exception.ResourceNotFoundException;
import com.safemenu.api.mapper.EntityMapper;
import com.safemenu.api.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DishService {

    private final DishRepository dishRepository;
    private final MenuCategoryService categoryService;
    private final IngredientService ingredientService;
    private final EntityMapper mapper;

    public DishResponse findById(Long id) {
        return mapper.toDishResponse(getEntityById(id));
    }

    public List<DishResponse> findByCategory(Long categoryId) {
        return dishRepository.findByCategoryId(categoryId).stream()
                .map(mapper::toDishResponse)
                .toList();
    }

    /**
     * Paginated menu for a restaurant — cached for performance.
     */
    @Cacheable(value = "restaurantMenus", key = "#restaurantId + '-' + #pageable.pageNumber")
    public Page<DishResponse> findByRestaurant(Long restaurantId, Pageable pageable) {
        return dishRepository.findByCategoryRestaurantId(restaurantId, pageable)
                .map(mapper::toDishResponse);
    }

    /**
     * Core allergen safety feature: returns only dishes that are safe
     * for someone who needs to avoid specific allergens.
     *
     * Example: GET /api/v1/dishes/safe?restaurantId=1&exclude=MILK,NUTS
     * → returns all dishes at that restaurant that contain neither milk nor nuts.
     */
    @Cacheable(value = "safeDishes", key = "#restaurantId + '-' + #excludedAllergens")
    public List<DishResponse> findSafeDishes(Long restaurantId, Set<AllergenType> excludedAllergens) {
        return dishRepository.findSafeDishes(restaurantId, excludedAllergens).stream()
                .map(mapper::toDishResponse)
                .toList();
    }

    public List<DishResponse> findVegetarian(Long restaurantId) {
        return dishRepository.findVegetarianDishes(restaurantId).stream()
                .map(mapper::toDishResponse)
                .toList();
    }

    public List<DishResponse> findVegan(Long restaurantId) {
        return dishRepository.findVeganDishes(restaurantId).stream()
                .map(mapper::toDishResponse)
                .toList();
    }

    @Transactional
    @CacheEvict(value = {"restaurantMenus", "safeDishes"}, allEntries = true)
    public DishResponse create(DishRequest request) {
        MenuCategory category = categoryService.getEntityById(request.getCategoryId());
        Set<Ingredient> ingredients = ingredientService.getEntitiesByIds(request.getIngredientIds());

        Dish dish = Dish.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .vegetarian(request.isVegetarian())
                .vegan(request.isVegan())
                .category(category)
                .ingredients(ingredients)
                .build();

        return mapper.toDishResponse(dishRepository.save(dish));
    }

    @Transactional
    @CacheEvict(value = {"restaurantMenus", "safeDishes"}, allEntries = true)
    public DishResponse update(Long id, DishRequest request) {
        Dish dish = getEntityById(id);
        Set<Ingredient> ingredients = ingredientService.getEntitiesByIds(request.getIngredientIds());

        dish.setName(request.getName());
        dish.setDescription(request.getDescription());
        dish.setPrice(request.getPrice());
        dish.setVegetarian(request.isVegetarian());
        dish.setVegan(request.isVegan());
        dish.setIngredients(ingredients);

        return mapper.toDishResponse(dishRepository.save(dish));
    }

    @Transactional
    @CacheEvict(value = {"restaurantMenus", "safeDishes"}, allEntries = true)
    public void delete(Long id) {
        Dish dish = getEntityById(id);
        dishRepository.delete(dish);
    }

    @Transactional
    @CacheEvict(value = {"restaurantMenus", "safeDishes"}, allEntries = true)
    public DishResponse toggleAvailability(Long id) {
        Dish dish = getEntityById(id);
        dish.setAvailable(!dish.isAvailable());
        return mapper.toDishResponse(dishRepository.save(dish));
    }

    private Dish getEntityById(Long id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish", id));
    }
}
