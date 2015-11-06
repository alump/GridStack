package org.vaadin.alump.gridstack.client;

import com.google.gwt.core.client.JavaScriptObject;
import org.vaadin.alump.gridstack.client.shared.GridStackOptions;

/**
 * Offer options JavaScriptObject
 */
public class GwtGridStackOptions extends JavaScriptObject {

    public final static native GwtGridStackOptions create()
    /*-{
       return {};
    }-*/;

    protected GwtGridStackOptions() {

    }

    public final static GwtGridStackOptions createFrom(GridStackOptions options) {
        GwtGridStackOptions obj = create();
        if(options.alwaysShowResizeHandle != null) {
            obj.setAlwaysShowResizeHandle(options.alwaysShowResizeHandle);
        }
        if(options.animate != null) {
            obj.setAnimate(options.animate);
        }
        if(options.cellHeight != null) {
            obj.setCellHeight(options.cellHeight);
        }
        if(options.height != null) {
            obj.setHeight(options.height);
        }
        if(options.minWidth != null) {
            obj.setMinWidth(options.minWidth);
        }
        if(options.staticGrid != null) {
            obj.setStaticGrid(options.staticGrid);
        }
        if(options.verticalMargin != null) {
            obj.setVerticalMargin(options.verticalMargin);
        }
        if(options.width != null) {
            obj.setWidth(options.width);
        }
        return obj;
    }

    public final native Boolean isAlwaysShowResizeHandle()
    /*-{
        return this.always_show_resize_handle;
    }-*/;

    public final native void setAlwaysShowResizeHandle(Boolean show)
    /*-{
        this.always_show_resize_handle = show;
    }-*/;

    public final native Boolean isAnimate()
    /*-{
        return this.animate;
    }-*/;

    public final native void setAnimate(Boolean animate)
    /*-{
        this.animate = animate;
    }-*/;

    public final native Integer getCellHeight()
    /*-{
        return this.cell_height;
    }-*/;

    public final native void setCellHeight(Integer height)
    /*-{
        this.cell_height = height;
    }-*/;

    public final native Integer getHeight()
    /*-{
        return this.height;
    }-*/;

    public final native void setHeight(Integer height)
    /*-{
        this.height = height;
    }-*/;

    public final native Integer getMinWidth()
    /*-{
        return this.min_width;
    }-*/;

    public final native void setMinWidth(Integer width)
    /*-{
        this.min_width = width;
    }-*/;

    public final native Boolean isStaticGrid()
    /*-{
        return this.static_grid;
    }-*/;

    public final native void setStaticGrid(Boolean staticGrid)
    /*-{
        this.static_grid = staticGrid;
    }-*/;

    public final native Integer getVerticalMargin()
    /*-{
        return this.vertical_margin;
    }-*/;

    public final native void setVerticalMargin(Integer margin)
    /*-{
        this.vertical_margin = margin;
    }-*/;

    public final native Integer getWidth()
    /*-{
        return this.width;
    }-*/;

    public final native void setWidth(Integer width)
    /*-{
        this.width = width;
    }-*/;

}
