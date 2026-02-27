package com.safemenu.api.repository;

import com.safemenu.api.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByActiveTrue();

    List<Restaurant> findByNameContainingIgnoreCase(String name);
}
