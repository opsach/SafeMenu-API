import { useEffect, useMemo, useState } from 'react';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

const ALLERGENS = [
  'CELERY',
  'CEREALS_WITH_GLUTEN',
  'CRUSTACEANS',
  'EGGS',
  'FISH',
  'LUPIN',
  'MILK',
  'MOLLUSCS',
  'MUSTARD',
  'NUTS',
  'PEANUTS',
  'SESAME',
  'SOYBEANS',
  'SULPHUR_DIOXIDE'
];

const currency = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'EUR'
});

export function App() {
  const [restaurants, setRestaurants] = useState([]);
  const [restaurantId, setRestaurantId] = useState('');
  const [selectedAllergens, setSelectedAllergens] = useState([]);
  const [dishes, setDishes] = useState([]);
  const [loadingRestaurants, setLoadingRestaurants] = useState(false);
  const [loadingDishes, setLoadingDishes] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    async function fetchRestaurants() {
      setLoadingRestaurants(true);
      setError('');

      try {
        const response = await fetch(`${API_BASE_URL}/api/v1/restaurants`);

        if (!response.ok) {
          throw new Error('Failed to load restaurants. Is the API running on port 8080?');
        }

        const data = await response.json();
        setRestaurants(data);

        if (data.length > 0) {
          setRestaurantId(String(data[0].id));
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setLoadingRestaurants(false);
      }
    }

    fetchRestaurants();
  }, []);

  const safeDishUrl = useMemo(() => {
    if (!restaurantId) {
      return '';
    }

    const params = new URLSearchParams({ restaurantId });

    if (selectedAllergens.length > 0) {
      params.set('exclude', selectedAllergens.join(','));
    }

    return `${API_BASE_URL}/api/v1/dishes/safe?${params.toString()}`;
  }, [restaurantId, selectedAllergens]);

  useEffect(() => {
    async function fetchDishes() {
      if (!safeDishUrl) {
        return;
      }

      setLoadingDishes(true);
      setError('');

      try {
        const response = await fetch(safeDishUrl);

        if (!response.ok) {
          throw new Error('Failed to load dishes. Check API data or URL.');
        }

        const data = await response.json();
        setDishes(data.content ?? data);
      } catch (err) {
        setError(err.message);
        setDishes([]);
      } finally {
        setLoadingDishes(false);
      }
    }

    fetchDishes();
  }, [safeDishUrl]);

  function toggleAllergen(allergen) {
    setSelectedAllergens((current) =>
      current.includes(allergen)
        ? current.filter((item) => item !== allergen)
        : [...current, allergen]
    );
  }

  return (
    <main className="page">
      <h1>SafeMenu Frontend Starter</h1>
      <p className="subtitle">Explore safe dishes by excluding allergens in real time.</p>

      <section className="card">
        <h2>Filters</h2>

        <label className="label" htmlFor="restaurant">
          Restaurant
        </label>
        <select
          id="restaurant"
          value={restaurantId}
          onChange={(event) => setRestaurantId(event.target.value)}
          disabled={loadingRestaurants || restaurants.length === 0}
        >
          {restaurants.length === 0 ? (
            <option value="">No restaurants found</option>
          ) : (
            restaurants.map((restaurant) => (
              <option key={restaurant.id} value={restaurant.id}>
                {restaurant.name}
              </option>
            ))
          )}
        </select>

        <p className="label">Exclude allergens</p>
        <div className="chips">
          {ALLERGENS.map((allergen) => {
            const selected = selectedAllergens.includes(allergen);
            return (
              <button
                key={allergen}
                type="button"
                className={selected ? 'chip chip--selected' : 'chip'}
                onClick={() => toggleAllergen(allergen)}
              >
                {allergen}
              </button>
            );
          })}
        </div>
      </section>

      <section className="card">
        <h2>Safe dishes</h2>
        <p className="endpoint">GET {safeDishUrl || '/api/v1/dishes/safe?restaurantId={id}'}</p>

        {loadingDishes ? <p>Loading dishes…</p> : null}
        {error ? <p className="error">{error}</p> : null}

        {!loadingDishes && !error && dishes.length === 0 ? (
          <p>No dishes match your filter.</p>
        ) : null}

        <ul className="dish-list">
          {dishes.map((dish) => (
            <li key={dish.id} className="dish-item">
              <strong>{dish.name}</strong>
              <span>{currency.format(Number(dish.price ?? 0))}</span>
              {dish.allergenWarning ? <small>{dish.allergenWarning}</small> : null}
            </li>
          ))}
        </ul>
      </section>
    </main>
  );
}
