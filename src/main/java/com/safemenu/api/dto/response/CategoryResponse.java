package com.safemenu.api.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private int displayOrder;
    private Long restaurantId;
    private String restaurantName;
    private int dishCount;
    private LocalDateTime createdAt;
}
