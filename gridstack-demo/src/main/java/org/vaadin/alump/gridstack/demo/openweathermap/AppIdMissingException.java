package org.vaadin.alump.gridstack.demo.openweathermap;

public class AppIdMissingException extends RuntimeException {
    public AppIdMissingException() {
        super("Please define appid key on openweathermap.properties file.");
    }
}
