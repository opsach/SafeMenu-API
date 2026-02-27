package com.safemenu.api.service;

import com.safemenu.api.dto.request.RestaurantRequest;
import com.safemenu.api.dto.response.RestaurantResponse;
import com.safemenu.api.entity.Restaurant;
import com.safemenu.api.exception.ResourceNotFoundException;
import com.safemenu.api.mapper.EntityMapper;
import com.safemenu.api.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final EntityMapper mapper;

    public List<RestaurantResponse> findAll() {
        return restaurantRepository.findAll().stream()
                .map(mapper::toRestaurantResponse)
                .toList();
    }

    public RestaurantResponse findById(Long id) {
        return mapper.toRestaurantResponse(getEntityById(id));
    }

    @Transactional
    public RestaurantResponse create(RestaurantRequest request) {
        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .description(request.getDescription())
                .build();

        return mapper.toRestaurantResponse(restaurantRepository.save(restaurant));
    }

    @Transactional
    public RestaurantResponse update(Long id, RestaurantRequest request) {
        Restaurant restaurant = getEntityById(id);
        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhone(request.getPhone());
        restaurant.setEmail(request.getEmail());
        restaurant.setDescription(request.getDescription());

        return mapper.toRestaurantResponse(restaurantRepository.save(restaurant));
    }

    @Transactional
    public void delete(Long id) {
        Restaurant restaurant = getEntityById(id);
        restaurantRepository.delete(restaurant);
    }

    /** Internal helper — used by other services to resolve FK references */
    public Restaurant getEntityById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", id));
    }
}
