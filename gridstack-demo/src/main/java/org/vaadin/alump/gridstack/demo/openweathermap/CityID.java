package org.vaadin.alump.gridstack.demo.openweathermap;

/**
 * http://bulk.openweathermap.org/sample/city.list.json.gz
 */
public enum CityID {
    SAN_JOSE(5392171), TURKU(633679), BERLIN(2950159), NEW_YORK(5128638), SHANGHAI(1796236), LONDON(2643743),
    SYDNEY(2147714), GLASGOW(2648579), PARIS(6455259);

    private final int id;

    CityID(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
