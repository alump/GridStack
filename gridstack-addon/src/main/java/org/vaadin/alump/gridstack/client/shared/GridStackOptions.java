package org.vaadin.alump.gridstack.client.shared;

import java.io.Serializable;

/**
 * Properties given to gridstack.js
 * See documentation at https://github.com/troolee/gridstack.js
 * Use null to keep default value of gridstack.js
 */
public class GridStackOptions implements Serializable {
    public Boolean alwaysShowResizeHandle = null;
    public Boolean animate = null;
    public Boolean auto = null;
    public Integer cellHeight = null;
    //TODO draggable
    //public String handle = null;
    //public String handleClass = null;
    public Integer height = null;
    public Boolean floating = null;
    //public String itemClass = null;
    public Integer minWidth = null;
    //public String placeHolderClass = null;
    //TODO resizable
    public Boolean staticGrid = null;
    public Integer verticalMargin = null;
    public Integer width = null;
}
