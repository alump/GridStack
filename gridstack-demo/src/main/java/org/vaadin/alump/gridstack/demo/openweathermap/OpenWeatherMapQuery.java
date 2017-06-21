package org.vaadin.alump.gridstack.demo.openweathermap;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by alump on 24/03/2017.
 */
public class OpenWeatherMapQuery {

    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();

    public OpenWeatherMapQuery() {

    }

    protected static class OpenWeatherMapQueryUrl extends GenericUrl {
        @Key
        private String id;

        @Key
        private String units;

        @Key
        private String appid;

        private OpenWeatherMapQueryUrl() {
            super("http://api.openweathermap.org/data/2.5/group");
        }

        public static OpenWeatherMapQueryUrl forCities(String appid, Collection<CityID> cities) {
            OpenWeatherMapQueryUrl url = new OpenWeatherMapQueryUrl();
            url.id = cities.stream().map(CityID::getId).map(id -> id.toString()).collect(Collectors.joining(","));
            url.units = "metric";
            url.appid = appid;
            return url;
        }
    }

    public void run(OpenWeatherMapListener listener) throws IOException {
        run(Arrays.stream(CityID.values()).collect(Collectors.toList()), listener);
    }

    private Optional<Properties> readProperties() {
        try {
            Properties properties = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("openweathermap.properties");
            properties.load(inputStream);
            return Optional.of(properties);
        } catch(Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void run(Collection<CityID> cities, OpenWeatherMapListener listener) throws IOException {

        Optional<String> appid = readProperties().map(p -> (String)p.get("appid")).filter(v -> !v.isEmpty());
        if(!appid.isPresent()) {
            throw new AppIdMissingException();
        }

        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) throws IOException {
                        request.setParser(new JsonObjectParser(JSON_FACTORY));
                    }
                });
        OpenWeatherMapQueryUrl url = OpenWeatherMapQueryUrl.forCities(appid.get(), cities);
        HttpRequest request = requestFactory.buildGetRequest(url);
        try {
            WeatherResponse response = parseResponse(request.execute());
            listener.onOpenWeatherResponse(response);
        } catch(HttpResponseException e) {
            System.err.println("HTTP response error, with appid " + appid.get());
            throw e;
        }
    }

    private WeatherResponse parseResponse(HttpResponse response) throws IOException {
        OpenWeatherMapResponse data = response.parseAs(OpenWeatherMapResponse.class);

        return new WeatherResponse(data.getList());
    }
}
