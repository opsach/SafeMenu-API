package com.safemenu.api.entity;

import com.safemenu.api.enums.AllergenType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "dishes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dish extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    @Column(name = "is_available")
    @Builder.Default
    private boolean available = true;

    @Column(name = "is_vegetarian")
    @Builder.Default
    private boolean vegetarian = false;

    @Column(name = "is_vegan")
    @Builder.Default
    private boolean vegan = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private MenuCategory category;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "dish_ingredients",
            joinColumns = @JoinColumn(name = "dish_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    @Builder.Default
    private Set<Ingredient> ingredients = new HashSet<>();

    /**
     * Automatically computes all allergens present in this dish
     * by aggregating allergens from each ingredient.
     * This is the core business logic of the platform.
     */
    @Transient
    public Set<AllergenType> getComputedAllergens() {
        return ingredients.stream()
                .flatMap(ingredient -> ingredient.getAllergens().stream())
                .collect(Collectors.toSet());
    }
}
