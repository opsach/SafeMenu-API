package com.safemenu.api.service;

import com.safemenu.api.dto.request.IngredientRequest;
import com.safemenu.api.dto.response.IngredientResponse;
import com.safemenu.api.entity.Ingredient;
import com.safemenu.api.exception.DuplicateResourceException;
import com.safemenu.api.exception.ResourceNotFoundException;
import com.safemenu.api.mapper.EntityMapper;
import com.safemenu.api.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final EntityMapper mapper;

    public List<IngredientResponse> findAll() {
        return ingredientRepository.findAll().stream()
                .map(mapper::toIngredientResponse)
                .toList();
    }

    public IngredientResponse findById(Long id) {
        return mapper.toIngredientResponse(getEntityById(id));
    }

    public List<IngredientResponse> search(String query) {
        return ingredientRepository.findByNameContainingIgnoreCase(query).stream()
                .map(mapper::toIngredientResponse)
                .toList();
    }

    @Transactional
    public IngredientResponse create(IngredientRequest request) {
        if (ingredientRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException(
                    "Ingredient already exists with name: " + request.getName());
        }

        Ingredient ingredient = Ingredient.builder()
                .name(request.getName())
                .description(request.getDescription())
                .allergens(request.getAllergens())
                .build();

        return mapper.toIngredientResponse(ingredientRepository.save(ingredient));
    }

    @Transactional
    public IngredientResponse update(Long id, IngredientRequest request) {
        Ingredient ingredient = getEntityById(id);
        ingredient.setName(request.getName());
        ingredient.setDescription(request.getDescription());
        ingredient.setAllergens(request.getAllergens());

        return mapper.toIngredientResponse(ingredientRepository.save(ingredient));
    }

    @Transactional
    public void delete(Long id) {
        Ingredient ingredient = getEntityById(id);
        ingredientRepository.delete(ingredient);
    }

    public Ingredient getEntityById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", id));
    }

    public Set<Ingredient> getEntitiesByIds(Set<Long> ids) {
        Set<Ingredient> found = ingredientRepository.findByIdIn(ids);
        if (found.size() != ids.size()) {
            throw new ResourceNotFoundException("One or more ingredients not found");
        }
        return found;
    }
}
