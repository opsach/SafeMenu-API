package com.safemenu.api.dto.request;

import com.safemenu.api.enums.AllergenType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientRequest {

    @NotBlank(message = "Ingredient name is required")
    private String name;

    private String description;

    @Builder.Default
    private Set<AllergenType> allergens = new HashSet<>();
}
