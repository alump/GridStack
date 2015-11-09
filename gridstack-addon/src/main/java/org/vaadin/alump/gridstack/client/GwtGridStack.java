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
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import org.vaadin.alump.gridstack.client.shared.GridStackChildOptions;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

public class GwtGridStack extends ComplexPanel {

    private final static Logger LOGGER = Logger.getLogger(GwtGridStack.class.getName());

    boolean initialized = false;

    private static int idCounter = 0;
    public final String elementId;

    protected GwtGridStackMoveHandler moveHandler = null;

    protected long lastEvent = 0;
    protected boolean draggedOrResized = false;

    public final static long DISABLE_CLICK_AFTER_EVENT_MS = 100L;

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

    public void setOptions(Integer width, Integer height, GwtGridStackOptions options) {
        if(!initialized) {
            if(width != null) {
                getElement().setAttribute("data-gs-width", width.toString());
            }
            if(height != null) {
                getElement().setAttribute("data-gs-height", height.toString());
            }
            initializeGridStack(options);
            initialized = true;
        } else {
            //TODO
        }
    }

    @Override
    public void add(Widget widget) {
        add(widget, new GridStackChildOptions());
    }

    public void add(Widget widget, GridStackChildOptions info) {
        Element wrapper = createWrapper(info);
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
            Element wrapper = widget.getElement().getParentElement().getParentElement();
            removeWidgetWrapperFromGridStack(wrapper);
            wrapper.removeFromParent();
        }
        return super.remove(widget);
    }

    protected Element createWrapper(GridStackChildOptions info) {
        Element wrapper = Document.get().createDivElement();
        wrapper.addClassName("grid-stack-item");
        if(info.x >= 0 && info.y >= 0) {
            wrapper.setAttribute("data-gs-x", Integer.toString(info.x));
            wrapper.setAttribute("data-gs-y", Integer.toString(info.y));
        } else {
            wrapper.setAttribute("data-gs-auto-position", "yes");
        }
        wrapper.setAttribute("data-gs-width", Integer.toString(info.width));
        wrapper.setAttribute("data-gs-height", Integer.toString(info.height));

        if(info.minWidth != null) {
            wrapper.setAttribute("data-gs-min-width", Integer.toString(info.minWidth.intValue()));
        }
        if(info.maxWidth != null) {
            wrapper.setAttribute("data-gs-max-width", Integer.toString(info.maxWidth.intValue()));
        }
        if(info.minHeight != null) {
            wrapper.setAttribute("data-gs-min-height", Integer.toString(info.minHeight.intValue()));
        }
        if(info.maxHeight != null) {
            wrapper.setAttribute("data-gs-max-width", Integer.toString(info.maxHeight.intValue()));
        }

        if(info.locked) {
            wrapper.setAttribute("data-gs-locked", "yes");
        }

        Element content = Document.get().createDivElement();
        content.addClassName("grid-stack-item-content");
        wrapper.appendChild(content);

        Element dragHandle = Document.get().createDivElement();
        dragHandle.addClassName("grid-stack-item-drag-handle");
        dragHandle.getStyle().setDisplay(Display.NONE);
        wrapper.appendChild(dragHandle);

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

    protected void onGridStackDragStart(Event event) {
        updateEventFlag(true);
    }

    protected void onGridStackDragStop(Event event) {
        updateEventFlag(false);
    }

    protected void onGridStackResizeStart(Event event) {
        updateEventFlag(true);
    }

    protected void onGridStackResizeStop(Event event) {
        updateEventFlag(false);
    }

    protected void updateEventFlag(boolean start) {
        draggedOrResized = start;
        lastEvent = new Date().getTime();
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
                that.@org.vaadin.alump.gridstack.client.GwtGridStack::onGridStackChange(*)(e, items);
            });
            element.on('dragstart', function(e, items) {
                that.@org.vaadin.alump.gridstack.client.GwtGridStack::onGridStackDragStart(*)(e);
            });
            element.on('dragstop', function(e, items) {
                that.@org.vaadin.alump.gridstack.client.GwtGridStack::onGridStackDragStop(*)(e);
            });
            element.on('resizestart', function(e, items) {
                that.@org.vaadin.alump.gridstack.client.GwtGridStack::onGridStackResizeStart(*)(e);
            });
            element.on('resizestop', function(e, items) {
                that.@org.vaadin.alump.gridstack.client.GwtGridStack::onGridStackResizeStop(*)(e);
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
        updateWidgetSizeLimits(wrapper, GwtGridSizeLimits.create(options));
        setLocked(wrapper, options.locked);
    }

    protected native final void updateWidgetWrapper(Element element, int x, int y, int width, int height)
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            if(x >= 0 && y >= 0) {
                grid.update(element, x, y, width, height);
            } else {
                grid.resize(element, width, height);
            }
        });
    }-*/;

    protected native final void updateWidgetSizeLimits(Element element, GwtGridSizeLimits values)
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            grid.min_width(element, values.min_width);
            //grid.max_width(element, values.max_width);
            grid.min_height(element, values.min_height);
            //grid.max_height(element, values.max_height);
        });
    }-*/;

    protected native final void setLocked(Element element, boolean locked)
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            grid.locked(element, locked);
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

    public boolean isClickOk() {
        return !draggedOrResized && new Date().getTime() > (lastEvent + DISABLE_CLICK_AFTER_EVENT_MS);
    }
}