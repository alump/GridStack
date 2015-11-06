/**
 * GwtGridStackChangedItem.java (GridStackLayout)
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
import com.google.gwt.dom.client.Element;

/**
 * Item object in changed event
 */
public class GwtGridStackChangedItem extends JavaScriptObject {

    protected GwtGridStackChangedItem() {

    }

    public final native int getX()
    /*-{
        return this.x;
    }-*/;

    public final native int getY()
    /*-{
        return this.y;
    }-*/;

    public final native int getWidth()
    /*-{
        return this.width;
    }-*/;

    public final native int getHeight()
    /*-{
        return this.height;
    }-*/;

    public final native Element getElement()
    /*-{
        return this.el[0];
    }-*/;
}
