package org.vaadin.alump.gridstack.demo.openweathermap;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

/**
 * Created by alump on 24/03/2017.
 */
public class WeatherPresentation extends CssLayout {

    public WeatherPresentation(OpenWeatherMapEntry entry) {
        addStyleName("weather-presentation");

        Label name = new Label(entry.getName()
                + entry.getWeatherString().map(w -> " <span class=\"weather\">" + w + "</span>").orElse(""));
        name.setContentMode(ContentMode.HTML);
        name.addStyleName("name-and-weather");
        name.setWidth(100, Unit.PERCENTAGE);

        Label temperature = new Label(entry.getTemperatureString());
        temperature.addStyleName("temperature");
        temperature.setWidth(100, Unit.PERCENTAGE);

        addComponents(name, temperature);
    }
}
