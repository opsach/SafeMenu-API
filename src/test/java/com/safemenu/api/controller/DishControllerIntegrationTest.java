package com.safemenu.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safemenu.api.dto.request.DishRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DishControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/dishes/{id} — returns dish with auto-computed allergens")
    void shouldReturnDishWithAllergens() throws Exception {
        // Dish 1 = Prawn Cocktail (prawns → CRUSTACEANS, eggs → EGGS)
        mockMvc.perform(get("/api/v1/dishes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Prawn Cocktail"))
                .andExpect(jsonPath("$.allergens", hasSize(2)))
                .andExpect(jsonPath("$.allergens", containsInAnyOrder("CRUSTACEANS", "EGGS")))
                .andExpect(jsonPath("$.allergenWarning", containsString("Contains")));
    }

    @Test
    @DisplayName("GET /api/v1/dishes/safe — filters out dishes with excluded allergens")
    void shouldReturnSafeDishes() throws Exception {
        // Exclude MILK → should NOT return Chicken Supreme, Pasta Primavera, Chocolate Fondant, Lemon Posset
        mockMvc.perform(get("/api/v1/dishes/safe")
                        .param("restaurantId", "1")
                        .param("exclude", "MILK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", not(hasItem("Chicken Supreme"))))
                .andExpect(jsonPath("$[*].name", not(hasItem("Chocolate Fondant"))))
                .andExpect(jsonPath("$[*].name", hasItem("Prawn Cocktail")))
                .andExpect(jsonPath("$[*].name", hasItem("Sesame Salmon Bowl")));
    }

    @Test
    @DisplayName("GET /api/v1/dishes/restaurant/{id} — returns paginated menu")
    void shouldReturnPaginatedMenu() throws Exception {
        mockMvc.perform(get("/api/v1/dishes/restaurant/1")
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements").value(7))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    @DisplayName("POST /api/v1/dishes — creates dish and computes allergens from ingredients")
    void shouldCreateDishWithComputedAllergens() throws Exception {
        DishRequest request = DishRequest.builder()
                .name("Peanut Prawn Stir-Fry")
                .description("Wok-fried prawns in peanut oil")
                .price(new BigDecimal("19.50"))
                .categoryId(2L)
                .ingredientIds(Set.of(6L, 8L)) // Prawns (CRUSTACEANS) + Peanut oil (PEANUTS)
                .build();

        mockMvc.perform(post("/api/v1/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Peanut Prawn Stir-Fry"))
                .andExpect(jsonPath("$.allergens", hasSize(2)))
                .andExpect(jsonPath("$.allergens", containsInAnyOrder("CRUSTACEANS", "PEANUTS")));
    }

    @Test
    @DisplayName("POST /api/v1/dishes — validation rejects missing required fields")
    void shouldRejectInvalidDish() throws Exception {
        DishRequest request = DishRequest.builder()
                .name("") // blank
                .price(null) // missing
                .categoryId(null) // missing
                .build();

        mockMvc.perform(post("/api/v1/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.price").exists())
                .andExpect(jsonPath("$.validationErrors.categoryId").exists());
    }

    @Test
    @DisplayName("GET /api/v1/dishes/999 — returns 404 for non-existent dish")
    void shouldReturn404ForMissingDish() throws Exception {
        mockMvc.perform(get("/api/v1/dishes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    @DisplayName("PATCH /api/v1/dishes/{id}/toggle-availability — toggles 86'd status")
    void shouldToggleAvailability() throws Exception {
        // First toggle: available → unavailable
        mockMvc.perform(patch("/api/v1/dishes/1/toggle-availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));

        // Second toggle: unavailable → available
        mockMvc.perform(patch("/api/v1/dishes/1/toggle-availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }
}
