package org.vaadin.alump.gridstack.demo.openweathermap;

/**
 * Weather rating
 */
public enum WeatherRating {
    NICE(1, "nice"), MEH(0, "meh"), SAD_PANDA(-1, "sad-panda");

    private final int level;
    private final String styleName;

    WeatherRating(int level, String styleName) {
        this.level = level;
        this.styleName = styleName;
    }

    public int getLevel() {
        return level;
    }

    public String getStyleName() {
        return styleName;
    }

    public static int resolveLevel(WeatherRating rating) {
        return rating.level;
    }
}
