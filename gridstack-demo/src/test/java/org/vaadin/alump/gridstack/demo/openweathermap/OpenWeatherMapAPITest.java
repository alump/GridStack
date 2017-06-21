package org.vaadin.alump.gridstack.demo.openweathermap;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Just simple test to make sure API works
 */
public class OpenWeatherMapAPITest {

    @Test
    public void simpleApiTest() throws IOException, InterruptedException {
        try {
            OpenWeatherMapQuery query = new OpenWeatherMapQuery();
            AtomicBoolean responseReceived = new AtomicBoolean(false);
            AtomicInteger responseRows = new AtomicInteger(0);

            query.run(response -> {
                responseReceived.set(true);
                responseRows.set(response.getWeathers().size());
                response.getWeathers().forEach(w -> {
                    System.out.println("Weather " + w.getName() + " " + w.getTemperatureString());
                });
            });
            int waitMilliseconds = 5000;
            while ((waitMilliseconds -= 250) > 0) {
                Thread.sleep(250);
                if (responseReceived.get()) {
                    break;
                }
            }
            Assert.assertTrue(responseReceived.get());
            Assert.assertEquals(CityID.values().length, responseRows.get());
        } catch(AppIdMissingException e) {
            //Allow this to happen, so test will be just skipped if appid isn't defined.
        }
    }
}
