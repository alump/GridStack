package org.vaadin.alump.gridstack.client;

import com.vaadin.client.ui.button.ButtonConnector;
import com.vaadin.shared.ui.Connect;

/**
 * Connector that adds modifications to VButton to make it work with GridStack
 */
@Connect(org.vaadin.alump.gridstack.GridStackButton.class)
public class GridStackButtonConnector extends ButtonConnector {

    public GwtGridStackButton getWidget() {
        return (GwtGridStackButton)super.getWidget();
    }

}
