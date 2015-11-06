/**
 * GwtGridStack.java (GridStackLayout)
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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import org.vaadin.alump.gridstack.client.shared.GridStackChildOptions;

import java.util.Iterator;
import java.util.logging.Logger;

public class GwtGridStack extends ComplexPanel {

    private final static Logger LOGGER = Logger.getLogger(GwtGridStack.class.getName());

    boolean initialized = false;

    private static int idCounter = 0;
    public final String elementId;

    protected GwtGridStackMoveHandler moveHandler = null;

    public interface GwtGridStackMoveHandler {
        void onWidgetsMoved(Widget[] widgets, GwtGridStackChangedItem[] data);
    }

	public GwtGridStack() {
        setElement(Document.get().createDivElement());
        setStyleName("grid-stack");

        elementId = "gridstack-" + (idCounter++);
        getElement().setId(elementId);
	}

    public boolean isInitialized() {
        return initialized;
    }

    public void setOptions(GwtGridStackOptions options) {
        if(!initialized) {
            initializeGridStack(options);
            initialized = true;
        } else {
            //TODO
        }
    }

    @Override
    public void add(Widget widget) {
        add(widget, 0, 0, 1, 1);
    }

    public void add(Widget widget, GridStackChildOptions info) {
        if(info != null) {
            add(widget, info.x, info.y, info.width, info.height);
        } else {
            add(widget);
        }
    }

    public void add(Widget widget, int x, int y, int width, int height) {
        Element wrapper = createWrapper(x, y, width, height);
        if(initialized) {
            addWidgetWrapperToGridStack(wrapper);
        } else {
            getElement().appendChild(wrapper);
        }
        super.add(widget, wrapper.getFirstChildElement());
    }

    @Override
    public boolean remove(Widget widget) {
        if(initialized) {
            removeWidgetWrapperFromGridStack(widget.getElement().getParentElement().getParentElement());
        }
        return super.remove(widget);
    }

    protected Element createWrapper(int x, int y, int width, int height) {
        Element wrapper = Document.get().createDivElement();
        wrapper.addClassName("grid-stack-item");
        wrapper.setAttribute("data-gs-x", Integer.toString(x));
        wrapper.setAttribute("data-gs-y", Integer.toString(y));
        wrapper.setAttribute("data-gs-width", Integer.toString(width));
        wrapper.setAttribute("data-gs-height", Integer.toString(height));

        Element content = Document.get().createDivElement();
        content.addClassName("grid-stack-content");
        wrapper.appendChild(content);

        return wrapper;
    }

    public Element getWrapper(Widget child) {
        if(child.getParent() == this) {
           throw new IllegalArgumentException("Given widget is not child of this GridStack");
        }
        return child.getElement().getParentElement();
    }

    protected void onGridStackChange(Event event, GwtGridStackChangedItem[] items) {
        Widget widgets[] = new Widget[items.length];

        for(int i = 0; i < items.length; ++i) {
            GwtGridStackChangedItem item = items[i];
            Widget child = mapElementToWidget(item.getElement());
            if(child == null) {
                LOGGER.severe("Could not map changed event to child");
                return;
            } else if(moveHandler != null) {
                widgets[i] = child;
            }
        }

        moveHandler.onWidgetsMoved(widgets, items);
    }

    protected Widget mapElementToWidget(Element element) {
        Iterator<Widget> iter = getChildren().iterator();
        while(iter.hasNext()) {
            Widget child = iter.next();
            if(element.isOrHasChild(child.getElement())) {
                return child;
            }
        }
        return null;
    }

    protected native void initializeGridStack(GwtGridStackOptions options)
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        var that = this;

        $wnd.$(function () {
            var element = $wnd.$('#' + elementId);
            element.gridstack(options);
            element.on('change', function(e, items) {
                console.log('change!');
                console.log(items);
                that.@org.vaadin.alump.gridstack.client.GwtGridStack::onGridStackChange(*)(e, items);
            });
        });
    }-*/;

    protected native void addWidgetWrapperToGridStack(Element element)
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            grid.add_widget(element);
        });
    }-*/;

    protected native void removeWidgetWrapperFromGridStack(Element element)
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            grid.remove_widget(element, false);
        });
    }-*/;

    public void setMoveHandler(GwtGridStackMoveHandler handler) {
        moveHandler = handler;
    }


    public void updateChild(Widget widget, GridStackChildOptions options) {
        Element wrapper = widget.getElement().getParentElement().getParentElement();
        updateWidgetWrapper(wrapper, options.x, options.y, options.width, options.height);
    }

    protected native final void updateWidgetWrapper(Element element, int x, int y, int width, int height)
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            grid.update(element, x, y, width, height);
        });
    }-*/;

    public void commit() {
        nativeCommit();
    }

    protected native final void nativeCommit()
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            grid.commit();
        });
    }-*/;

    public void batchUpdate() {
        nativeBatchUpdate();
    }

    protected native final void nativeBatchUpdate()
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            grid.batch_update();
        });
    }-*/;
}