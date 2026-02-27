package com.safemenu.api.dto.response;

import com.safemenu.api.enums.AllergenType;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientResponse {

    private Long id;
    private String name;
    private String description;
    private Set<AllergenType> allergens;
}
