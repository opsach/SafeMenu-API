package com.safemenu.api.repository;

import com.safemenu.api.entity.Dish;
import com.safemenu.api.enums.AllergenType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

    List<Dish> findByCategoryId(Long categoryId);

    Page<Dish> findByCategoryRestaurantId(Long restaurantId, Pageable pageable);

    List<Dish> findByCategoryRestaurantIdAndAvailableTrue(Long restaurantId);

    /**
     * Find dishes that do NOT contain any of the specified allergens.
     * This is the key "safe dining" query — e.g. "show me everything without gluten or nuts".
     */
    @Query("""
            SELECT DISTINCT d FROM Dish d
            JOIN d.category c
            WHERE c.restaurant.id = :restaurantId
              AND d.available = true
              AND d.id NOT IN (
                  SELECT d2.id FROM Dish d2
                  JOIN d2.ingredients i
                  JOIN i.allergens a
                  WHERE a IN :excludedAllergens
              )
            """)
    List<Dish> findSafeDishes(
            @Param("restaurantId") Long restaurantId,
            @Param("excludedAllergens") Set<AllergenType> excludedAllergens
    );

    @Query("SELECT d FROM Dish d WHERE d.category.restaurant.id = :restaurantId AND d.vegetarian = true AND d.available = true")
    List<Dish> findVegetarianDishes(@Param("restaurantId") Long restaurantId);

    @Query("SELECT d FROM Dish d WHERE d.category.restaurant.id = :restaurantId AND d.vegan = true AND d.available = true")
    List<Dish> findVeganDishes(@Param("restaurantId") Long restaurantId);
}
