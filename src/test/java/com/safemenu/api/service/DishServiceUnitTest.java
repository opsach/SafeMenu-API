package com.safemenu.api.service;

import com.safemenu.api.dto.request.DishRequest;
import com.safemenu.api.dto.response.DishResponse;
import com.safemenu.api.entity.Dish;
import com.safemenu.api.entity.Ingredient;
import com.safemenu.api.entity.MenuCategory;
import com.safemenu.api.enums.AllergenType;
import com.safemenu.api.mapper.EntityMapper;
import com.safemenu.api.repository.DishRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DishServiceUnitTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private MenuCategoryService categoryService;

    @Mock
    private IngredientService ingredientService;

    @Mock
    private EntityMapper mapper;

    @InjectMocks
    private DishService dishService;

    @Test
    void updateShouldApplyCategoryFromRequest() {
        Long dishId = 1L;
        Long newCategoryId = 7L;

        Dish existingDish = Dish.builder()
                .id(dishId)
                .name("Old Dish")
                .build();

        MenuCategory newCategory = MenuCategory.builder().id(newCategoryId).name("Mains").build();
        Set<Ingredient> ingredients = Set.of(Ingredient.builder().id(1L).name("Butter").build());

        DishRequest request = DishRequest.builder()
                .name("Updated Dish")
                .description("Updated")
                .price(new BigDecimal("12.50"))
                .vegetarian(false)
                .vegan(false)
                .categoryId(newCategoryId)
                .ingredientIds(Set.of(1L))
                .build();

        when(dishRepository.findById(dishId)).thenReturn(Optional.of(existingDish));
        when(categoryService.getEntityById(newCategoryId)).thenReturn(newCategory);
        when(ingredientService.getEntitiesByIds(request.getIngredientIds())).thenReturn(ingredients);
        when(dishRepository.save(any(Dish.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDishResponse(any(Dish.class))).thenReturn(new DishResponse());

        dishService.update(dishId, request);

        ArgumentCaptor<Dish> captor = ArgumentCaptor.forClass(Dish.class);
        verify(dishRepository).save(captor.capture());

        Dish savedDish = captor.getValue();
        assertThat(savedDish.getCategory()).isEqualTo(newCategory);
        assertThat(savedDish.getName()).isEqualTo("Updated Dish");
    }

    @Test
    void buildSafeDishCacheKeyShouldBeOrderIndependent() {
        Set<AllergenType> firstOrder = new LinkedHashSet<>();
        firstOrder.add(AllergenType.MILK);
        firstOrder.add(AllergenType.PEANUTS);

        Set<AllergenType> secondOrder = new LinkedHashSet<>();
        secondOrder.add(AllergenType.PEANUTS);
        secondOrder.add(AllergenType.MILK);

        String key1 = DishService.buildSafeDishCacheKey(5L, firstOrder);
        String key2 = DishService.buildSafeDishCacheKey(5L, secondOrder);

        assertThat(key1).isEqualTo("5-MILK,PEANUTS");
        assertThat(key2).isEqualTo(key1);
    }
}
