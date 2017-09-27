package com.virginiatech.slapdash.slapdash.DataModelClasses;

/**
 * Created by nima on 10/3/16.
 */

public enum EventType {
    PLAY, FOOD, DRINK, RANDOM;

    private String text;
    public String getText() {
        return text;
    }

    static {
        PLAY.text = "Play";
        FOOD.text = "Food";
        DRINK.text = "Drink";
        RANDOM.text = "SlapDash";
    }
}
