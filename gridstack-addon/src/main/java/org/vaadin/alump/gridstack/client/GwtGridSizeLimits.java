/**
 * GwtSizeLimits.java (GridStackLayout)
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
import org.vaadin.alump.gridstack.client.shared.GridStackChildOptions;

/**
 * Object used to pass size limit values to native code
 */
public class GwtGridSizeLimits extends JavaScriptObject {

    public final static native GwtGridSizeLimits create()
    /*-{
        return {};
    }-*/;

    public final static GwtGridSizeLimits create(GridStackChildOptions options) {
        return create(options.minWidth, options.maxWidth, options.minHeight, options.maxHeight);
    }

    public final static GwtGridSizeLimits create(Integer minWidth, Integer maxWidth, Integer minHeight, Integer maxHeight) {
        GwtGridSizeLimits limits = create();
        if(minWidth != null) {
            limits.setMinWidth(minWidth.intValue());
        }
        if(maxWidth != null) {
            limits.setMaxWidth(maxWidth.intValue());
        }
        if(minHeight != null) {
            limits.setMinHeight(minHeight.intValue());
        }
        if(maxHeight != null) {
            limits.setMaxHeight(maxHeight);
        }
        return limits;
    }

    protected GwtGridSizeLimits() {

    }

    public final native int getMinWidth()
    /*-{
        return this.min_width;
    }-*/;

    public final native void setMinWidth(int width)
    /*-{
        this.min_width = width;
    }-*/;

    public final native int getMinHeight()
    /*-{
        return this.min_height;
    }-*/;

    public final native void setMinHeight(int height)
    /*-{
        this.min_height = height;
    }-*/;

    public final native int getMaxWidth()
    /*-{
        return this.max_width;
    }-*/;

    public final native void setMaxWidth(int width)
    /*-{
        this.max_width = width;
    }-*/;

    public final native int getMaxHeight()
    /*-{
        return this.max_height;
    }-*/;

    public final native void setMaxHeight(int height)
    /*-{
        this.max_height = height;
    }-*/;

}
