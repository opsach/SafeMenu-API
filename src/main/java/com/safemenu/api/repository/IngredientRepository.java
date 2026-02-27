package com.safemenu.api.repository;

import com.safemenu.api.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Optional<Ingredient> findByNameIgnoreCase(String name);

    List<Ingredient> findByNameContainingIgnoreCase(String name);

    @Query("SELECT i FROM Ingredient i WHERE i.id IN :ids")
    Set<Ingredient> findByIdIn(@Param("ids") Set<Long> ids);

    boolean existsByNameIgnoreCase(String name);
}
