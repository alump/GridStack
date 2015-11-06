/**
 * GwtGridStackOptions.java (GridStackLayout)
 *
 * Copyright 2015 Vaadin Ltd, Sami Viitanen <sami.viitanen@vaadin.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    public final native boolean isAlwaysShowResizeHandle()
    /*-{
        return this.always_show_resize_handle;
    }-*/;

    public final native void setAlwaysShowResizeHandle(boolean show)
    /*-{
        this.always_show_resize_handle = show;
    }-*/;

    public final native boolean isAnimate()
    /*-{
        return this.animate;
    }-*/;

    public final native void setAnimate(boolean animate)
    /*-{
        this.animate = animate;
    }-*/;

    public final native int getCellHeight()
    /*-{
        return this.cell_height;
    }-*/;

    public final native void setCellHeight(int height)
    /*-{
        this.cell_height = height;
    }-*/;

    public final native int getHeight()
    /*-{
        return this.height;
    }-*/;

    public final native void setHeight(int height)
    /*-{
        this.height = height;
    }-*/;

    public final native int getMinWidth()
    /*-{
        return this.min_width;
    }-*/;

    public final native void setMinWidth(int width)
    /*-{
        this.min_width = width;
    }-*/;

    public final native boolean isStaticGrid()
    /*-{
        return this.static_grid;
    }-*/;

    public final native void setStaticGrid(boolean staticGrid)
    /*-{
        this.static_grid = staticGrid;
    }-*/;

    public final native int getVerticalMargin()
    /*-{
        return this.vertical_margin;
    }-*/;

    public final native void setVerticalMargin(int margin)
    /*-{
        this.vertical_margin = margin;
    }-*/;

    public final native int getWidth()
    /*-{
        return this.width;
    }-*/;

    public final native void setWidth(int width)
    /*-{
        this.width = width;
    }-*/;

}
