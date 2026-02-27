package com.safemenu.api.dto.response;

import com.safemenu.api.enums.AllergenType;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DishResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean available;
    private boolean vegetarian;
    private boolean vegan;
    private String categoryName;
    private Long categoryId;

    /** Ingredients used in this dish */
    private Set<IngredientResponse> ingredients;

    /**
     * Auto-computed allergens aggregated from all ingredients.
     * This is the core value proposition — chefs add ingredients,
     * the system flags every applicable EU-14 allergen automatically.
     */
    private Set<AllergenType> allergens;

    /** Human-readable allergen warning string for display */
    private String allergenWarning;
}
