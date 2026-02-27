import { useEffect, useMemo, useState } from 'react';

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '');

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

function humanizeAllergen(allergen) {
  return allergen
    .toLowerCase()
    .split('_')
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ');
}

export function App() {
  const [restaurants, setRestaurants] = useState([]);
  const [restaurantId, setRestaurantId] = useState('');
  const [selectedAllergens, setSelectedAllergens] = useState([]);
  const [dishes, setDishes] = useState([]);
  const [loadingRestaurants, setLoadingRestaurants] = useState(false);
  const [loadingDishes, setLoadingDishes] = useState(false);
  const [error, setError] = useState('');
  const restaurantsUrl = API_BASE_URL ? `${API_BASE_URL}/api/v1/restaurants` : '/api/v1/restaurants';

  useEffect(() => {
    async function fetchRestaurants() {
      setLoadingRestaurants(true);
      setError('');

      try {
        const response = await fetch(restaurantsUrl);

        if (!response.ok) {
          throw new Error('Unable to load restaurants. Make sure the API is running and reachable.');
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
  }, [restaurantsUrl]);

  const safeDishUrl = useMemo(() => {
    if (!restaurantId) {
      return '';
    }

    const params = new URLSearchParams({ restaurantId });

    if (selectedAllergens.length > 0) {
      params.set('exclude', selectedAllergens.join(','));
    }

    const endpoint = `/api/v1/dishes/safe?${params.toString()}`;
    return API_BASE_URL ? `${API_BASE_URL}${endpoint}` : endpoint;
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
          throw new Error('Unable to load dishes. Check API connectivity and seeded data.');
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

  function clearAllergens() {
    setSelectedAllergens([]);
  }

  return (
    <main className="page">
      <header className="hero">
        <p className="eyebrow">SafeMenu</p>
        <h1>Find dishes that fit your allergies in seconds</h1>
        <p className="subtitle">
          Pick a restaurant, exclude allergens, and instantly see dishes that are safe to order.
        </p>
      </header>

      <section className="card filter-card">
        <div className="section-heading">
          <h2>Filters</h2>
          <span className="status-pill">
            {selectedAllergens.length} allergen{selectedAllergens.length === 1 ? '' : 's'} excluded
          </span>
        </div>

        <label className="label" htmlFor="restaurant">
          Restaurant
        </label>
        <div className="select-wrap">
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
          {loadingRestaurants ? <span className="hint">Loading restaurants…</span> : null}
        </div>

        <div className="allergens-header">
          <p className="label">Exclude allergens</p>
          <button
            className="link-button"
            type="button"
            onClick={clearAllergens}
            disabled={selectedAllergens.length === 0}
          >
            Clear all
          </button>
        </div>

        <div className="chips" role="list" aria-label="Allergen filters">
          {ALLERGENS.map((allergen) => {
            const selected = selectedAllergens.includes(allergen);
            return (
              <button
                key={allergen}
                type="button"
                role="listitem"
                className={selected ? 'chip chip--selected' : 'chip'}
                onClick={() => toggleAllergen(allergen)}
                aria-pressed={selected}
              >
                {humanizeAllergen(allergen)}
              </button>
            );
          })}
        </div>
      </section>

      <section className="card">
        <div className="section-heading">
          <h2>Safe dishes</h2>
          <span className="status-pill status-pill--soft">{dishes.length} result{dishes.length === 1 ? '' : 's'}</span>
        </div>

        <p className="endpoint">GET {safeDishUrl || '/api/v1/dishes/safe?restaurantId={id}'}</p>

        {loadingDishes ? <p className="info">Loading dishes…</p> : null}
        {error ? <p className="error">{error}</p> : null}

        {!loadingDishes && !error && dishes.length === 0 ? (
          <p className="info">No dishes match your current filters. Try removing one or more allergens.</p>
        ) : null}

        <ul className="dish-list">
          {dishes.map((dish) => (
            <li key={dish.id} className="dish-item">
              <div>
                <strong>{dish.name}</strong>
                {dish.allergenWarning ? <small>{dish.allergenWarning}</small> : null}
              </div>
              <span className="price">{currency.format(Number(dish.price ?? 0))}</span>
            </li>
          ))}
        </ul>
      </section>
    </main>
  );
}
