package org.vaadin.alump.gridstack.demo.openweathermap;

import com.google.api.client.util.Key;

import java.util.List;

/**
 * Created by alump on 24/03/2017.
 */
public class OpenWeatherMapResponse {

    @Key("cnt")
    private int count;

    @Key("list")
    private List<OpenWeatherMapEntry> list;

    public List<OpenWeatherMapEntry> getList() {
        return list;
    }
}
