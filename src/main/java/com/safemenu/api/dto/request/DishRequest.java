package com.safemenu.api.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DishRequest {

    @NotBlank(message = "Dish name is required")
    private String name;

    @Size(max = 1000, message = "Description must be under 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    private boolean vegetarian;

    private boolean vegan;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    /**
     * IDs of ingredients in this dish.
     * Allergens are auto-computed from these.
     */
    @Builder.Default
    private Set<Long> ingredientIds = new HashSet<>();
}
