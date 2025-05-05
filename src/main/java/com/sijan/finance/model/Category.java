package com.sijan.finance.model;

public enum Category {
    FOODANDBEVERAGES("Food and Beverages"),
    RENT("Rent"),
    UTILITIES("Utilities"),
    ENTERTAINMENT("Entertainment"),
    TRAVEL("Travel"),
    OTHER("Other");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
