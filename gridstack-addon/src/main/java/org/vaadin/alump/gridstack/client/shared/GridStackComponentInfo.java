package org.vaadin.alump.gridstack.client.shared;

import java.io.Serializable;

/**
 * Layouting metadata for child components
 */
public class GridStackComponentInfo implements Serializable {
    public int x = -1;
    public int y = -1;
    public int width = 1;
    public int height = 1;
    public boolean autoPosition = false;
}
