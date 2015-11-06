package org.vaadin.alump.gridstack.client.shared;

import java.io.Serializable;

/**
 * Properties given to gridstack.js
 */
public class GridStackProperties implements Serializable {
    public boolean staticGrid = false;
    public boolean floating = false;
    public int width = 1;
    public int cellHeight = 80;
    public int verticalMargin = 10;
}
