-- V2__seed_data.sql — Demo data for development

-- Restaurant
INSERT INTO restaurants (name, address, phone, email, description) VALUES
('The Green Kitchen', '42 Dame Street, Dublin 2', '+353 1 555 0123', 'info@greenkitchen.ie', 'Farm-to-table dining with a focus on seasonal Irish produce');

-- Categories
INSERT INTO menu_categories (name, description, display_order, restaurant_id) VALUES
('Starters', 'Light bites to begin your meal', 1, 1),
('Mains', 'Signature main courses', 2, 1),
('Desserts', 'Sweet endings', 3, 1);

-- Ingredients with allergen tags
INSERT INTO ingredients (name, description) VALUES
('Wheat flour', 'All-purpose wheat flour'),
('Butter', 'Irish dairy butter'),
('Eggs', 'Free-range hen eggs'),
('Whole milk', 'Full-fat pasteurised milk'),
('Salmon fillet', 'Atlantic salmon'),
('Prawns', 'Dublin Bay prawns'),
('Soy sauce', 'Fermented soybean condiment'),
('Peanut oil', 'Cold-pressed peanut oil'),
('Almonds', 'Whole blanched almonds'),
('Sesame seeds', 'White sesame seeds'),
('Parmesan', 'Parmigiano-Reggiano'),
('Olive oil', 'Extra virgin olive oil'),
('Cherry tomatoes', 'Vine-ripened cherry tomatoes'),
('Fresh basil', 'Italian sweet basil'),
('Chicken breast', 'Free-range chicken breast'),
('Cream', 'Heavy double cream'),
('Dark chocolate', 'Belgian 70% cocoa chocolate'),
('Sugar', 'Caster sugar'),
('Lemon', 'Fresh unwaxed lemons'),
('Mustard', 'Dijon mustard');

-- Allergen tags
INSERT INTO ingredient_allergens (ingredient_id, allergen) VALUES
(1, 'CEREALS_WITH_GLUTEN'),
(2, 'MILK'),
(3, 'EGGS'),
(4, 'MILK'),
(5, 'FISH'),
(6, 'CRUSTACEANS'),
(7, 'SOYBEANS'),
(8, 'PEANUTS'),
(9, 'NUTS'),
(10, 'SESAME'),
(11, 'MILK'),
(16, 'MILK'),
(17, 'MILK'),
(20, 'MUSTARD');

-- Dishes
INSERT INTO dishes (name, description, price, is_vegetarian, is_vegan, category_id) VALUES
('Prawn Cocktail', 'Dublin Bay prawns with Marie Rose sauce on baby gem', 14.50, false, false, 1),
('Tomato Bruschetta', 'Cherry tomatoes, basil, olive oil on sourdough toast', 10.00, true, false, 1),
('Sesame Salmon Bowl', 'Pan-seared salmon with soy glaze and sesame seeds over rice', 24.50, false, false, 2),
('Chicken Supreme', 'Free-range chicken breast in cream and mustard sauce', 22.00, false, false, 2),
('Pasta Primavera', 'Seasonal vegetables in olive oil and parmesan', 18.50, true, false, 2),
('Chocolate Fondant', 'Warm Belgian chocolate fondant with vanilla cream', 11.00, true, false, 3),
('Lemon Posset', 'Cream and lemon set dessert with almond crumble', 9.50, true, false, 3);

-- Dish ↔ Ingredient relationships
-- Prawn Cocktail: prawns, eggs (mayo), lemon
INSERT INTO dish_ingredients (dish_id, ingredient_id) VALUES (1, 6), (1, 3), (1, 19);
-- Tomato Bruschetta: wheat flour (bread), cherry tomatoes, basil, olive oil
INSERT INTO dish_ingredients (dish_id, ingredient_id) VALUES (2, 1), (2, 13), (2, 14), (2, 12);
-- Sesame Salmon Bowl: salmon, soy sauce, sesame seeds
INSERT INTO dish_ingredients (dish_id, ingredient_id) VALUES (3, 5), (3, 7), (3, 10);
-- Chicken Supreme: chicken, cream, mustard, butter
INSERT INTO dish_ingredients (dish_id, ingredient_id) VALUES (4, 15), (4, 16), (4, 20), (4, 2);
-- Pasta Primavera: wheat flour (pasta), olive oil, parmesan, cherry tomatoes
INSERT INTO dish_ingredients (dish_id, ingredient_id) VALUES (5, 1), (5, 12), (5, 11), (5, 13);
-- Chocolate Fondant: dark chocolate, butter, eggs, wheat flour, sugar
INSERT INTO dish_ingredients (dish_id, ingredient_id) VALUES (6, 17), (6, 2), (6, 3), (6, 1), (6, 18);
-- Lemon Posset: cream, lemon, sugar, almonds
INSERT INTO dish_ingredients (dish_id, ingredient_id) VALUES (7, 16), (7, 19), (7, 18), (7, 9);
