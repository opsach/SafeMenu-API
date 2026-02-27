package com.safemenu.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    @Min(value = 0, message = "Display order must be non-negative")
    private int displayOrder;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;
}
