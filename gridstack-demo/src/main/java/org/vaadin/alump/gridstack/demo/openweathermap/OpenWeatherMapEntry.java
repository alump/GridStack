package org.vaadin.alump.gridstack.demo.openweathermap;

import com.google.api.client.util.Key;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Created by alump on 24/03/2017.
 */
public class OpenWeatherMapEntry {

    private final static DecimalFormat TEMPERATURE_FORMAT = new DecimalFormat("0.0");

    public static class MainEntry {
        @Key("temp")
        protected Float temp;

        @Key("temp_min")
        protected Float tempMin;

        @Key("temp_max")
        protected Float tempMax;

        public Optional<Float> getTemperature() {
            return Optional.ofNullable(temp);
        }

        public Optional<Float> getTemperatureMin() {
            return Optional.ofNullable(tempMin);
        }

        public Optional<Float> getTemperatureMax() {
            return Optional.ofNullable(tempMax);
        }

        @Override
        public String toString() {
            return getTemperatureString(getTemperature()).orElse("n/a") +
                    getTemperatureString(getTemperatureMin()).map(s -> ", min " + s).orElse("") +
                    getTemperatureString(getTemperatureMax()).map(s -> ", max " + s).orElse("");

        }
    }

    public static class WeatherEntry {
        @Key("main")
        protected String main;

        @Key("description")
        protected String description;

        @Key("icon")
        protected String icon;

        public Optional<String> getMain() {
            return Optional.ofNullable(main);
        }

        public Optional<String> getDescription() {
            return Optional.ofNullable(description);
        }

        public WeatherRating getRating() {
            String value = getMain().orElse("").toLowerCase();

            switch(value) {
                case "clear":
                    return WeatherRating.NICE;
                case "haze":
                case "rain":
                    return WeatherRating.SAD_PANDA;
                case "clouds":
                case "mist":
                    return WeatherRating.MEH;
                default:
                    System.out.println("Unmapped " + value);
                    return WeatherRating.MEH;
            }
        }

        @Override
        public String toString() {
            return getDescription().orElseGet(() -> getMain().orElse("n/a"));
        }
    }

    @Key("id")
    private Integer id;

    @Key("name")
    private String name;

    @Key("main")
    private MainEntry main;

    @Key("weather")
    private List<WeatherEntry> weather;

    public OpenWeatherMapEntry() {

    }

    public String getName() {
        return name;
    }

    public Optional<Float> getTemperature() {
        return Optional.ofNullable(main).flatMap(m -> m.getTemperature());
    }

    public static Optional<String> getTemperatureString(Optional<Float> value) {
        return value.map(f -> TEMPERATURE_FORMAT.format(f) + " °C");
    }

    public String getTemperatureString() {
        return Optional.ofNullable(main).map(m -> m.toString()).orElse("n/a");
    }

    public Optional<String> getWeather() {
        return Optional.ofNullable(weather)
                .filter(w -> !w.isEmpty())
                .map(w -> w.iterator().next())
                .flatMap(m -> m.getMain());
    }

    public Optional<String> getWeatherString() {
        if(weather == null) {
            return Optional.empty();
        }

        StringJoiner sj = new StringJoiner(", ");
        weather.forEach(w -> sj.add(w.toString()));
        return Optional.of(sj.toString()).filter(s -> !s.isEmpty());
    }

    public WeatherRating getRating() {
        if(weather == null || weather.isEmpty()) {
            return  WeatherRating.MEH;
        }
        return weather.stream().map(w -> w.getRating()).sorted((a,b) -> Integer.compare(a.getLevel(), b.getLevel()))
                .findFirst().orElse(WeatherRating.MEH);
    }
}
