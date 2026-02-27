package com.safemenu.api.enums;

/**
 * EU Regulation No 1169/2011 — the 14 major food allergens
 * that must be declared on food labels and menus across the EU.
 */
public enum AllergenType {

    CELERY("Celery", "Including stalks, leaves, seeds, and celeriac"),
    CEREALS_WITH_GLUTEN("Cereals containing gluten", "Wheat, rye, barley, oats, spelt, kamut"),
    CRUSTACEANS("Crustaceans", "Crabs, lobster, prawns, shrimp, scampi"),
    EGGS("Eggs", "All egg-based products"),
    FISH("Fish", "All species of fish"),
    LUPIN("Lupin", "Lupin seeds and flour"),
    MILK("Milk", "Including lactose, all dairy products"),
    MOLLUSCS("Molluscs", "Mussels, oysters, squid, snails"),
    MUSTARD("Mustard", "Including mustard seeds, powder, and oil"),
    NUTS("Tree nuts", "Almonds, hazelnuts, walnuts, cashews, pecans, brazil nuts, pistachios, macadamia"),
    PEANUTS("Peanuts", "Including groundnuts and peanut oil"),
    SESAME("Sesame seeds", "Including sesame oil and paste (tahini)"),
    SOYBEANS("Soybeans", "Including soya, edamame, tofu, tempeh"),
    SULPHUR_DIOXIDE("Sulphur dioxide / sulphites", "At concentrations above 10mg/kg or 10mg/litre");

    private final String displayName;
    private final String description;

    AllergenType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
