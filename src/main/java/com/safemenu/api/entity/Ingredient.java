package com.safemenu.api.entity;

import com.safemenu.api.enums.AllergenType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    /**
     * EU-14 allergens associated with this ingredient.
     * E.g. "Flour" → [CEREALS_WITH_GLUTEN], "Butter" → [MILK]
     */
    @ElementCollection(targetClass = AllergenType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "ingredient_allergens", joinColumns = @JoinColumn(name = "ingredient_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "allergen")
    @Builder.Default
    private Set<AllergenType> allergens = new HashSet<>();

    @ManyToMany(mappedBy = "ingredients")
    @Builder.Default
    private Set<Dish> dishes = new HashSet<>();
}
