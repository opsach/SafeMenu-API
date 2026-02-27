package com.safemenu.api.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponse {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String description;
    private boolean active;
    private int categoryCount;
    private int dishCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
