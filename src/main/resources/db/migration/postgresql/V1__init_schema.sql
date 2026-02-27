-- V1__init_schema.sql
-- SafeMenu API — initial database schema

CREATE TABLE restaurants (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    address         VARCHAR(255) NOT NULL,
    phone           VARCHAR(50),
    email           VARCHAR(255),
    description     VARCHAR(500),
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP
);

CREATE TABLE menu_categories (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    description     VARCHAR(500),
    display_order   INT DEFAULT 0,
    restaurant_id   BIGINT NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP
);

CREATE TABLE ingredients (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL UNIQUE,
    description     VARCHAR(500),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP
);

CREATE TABLE ingredient_allergens (
    ingredient_id   BIGINT NOT NULL REFERENCES ingredients(id) ON DELETE CASCADE,
    allergen        VARCHAR(50) NOT NULL,
    PRIMARY KEY (ingredient_id, allergen)
);

CREATE TABLE dishes (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    description     VARCHAR(1000),
    price           DECIMAL(8,2) NOT NULL,
    is_available    BOOLEAN DEFAULT TRUE,
    is_vegetarian   BOOLEAN DEFAULT FALSE,
    is_vegan        BOOLEAN DEFAULT FALSE,
    category_id     BIGINT NOT NULL REFERENCES menu_categories(id) ON DELETE CASCADE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP
);

CREATE TABLE dish_ingredients (
    dish_id         BIGINT NOT NULL REFERENCES dishes(id) ON DELETE CASCADE,
    ingredient_id   BIGINT NOT NULL REFERENCES ingredients(id) ON DELETE CASCADE,
    PRIMARY KEY (dish_id, ingredient_id)
);

-- Indexes for common query patterns
CREATE INDEX idx_categories_restaurant ON menu_categories(restaurant_id);
CREATE INDEX idx_dishes_category ON dishes(category_id);
CREATE INDEX idx_dishes_available ON dishes(is_available);
CREATE INDEX idx_ingredient_allergens_allergen ON ingredient_allergens(allergen);
