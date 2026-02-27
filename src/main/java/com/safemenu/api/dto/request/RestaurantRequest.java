package com.safemenu.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantRequest {

    @NotBlank(message = "Restaurant name is required")
    @Size(max = 255, message = "Name must be under 255 characters")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 500, message = "Description must be under 500 characters")
    private String description;
}
