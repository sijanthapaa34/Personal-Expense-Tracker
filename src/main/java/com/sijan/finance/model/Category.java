package com.sijan.finance.model;

public enum Category {
    FOODANDBEVERAGES("Food and Beverages", 15000.0),
    RENT("Rent", 25000.0),
    UTILITIES("Utilities", 5000.0),
    ENTERTAINMENT("Entertainment", 4000.0),
    TRAVEL("Travel", 8000.0),
    OTHER("Other", 3000.0);

    private final String displayName;
    private final double maxBudget;

    Category(String displayName, double maxBudget) {
        this.displayName = displayName;
        this.maxBudget = maxBudget;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getMaxBudget() {
        return maxBudget;
    }

    public static Category fromDisplayName(String displayName) {
        for (Category category : Category.values()) {
            if (category.getDisplayName().equalsIgnoreCase(displayName)) {
                return category;
            }
        }
        return OTHER;
    }

}
