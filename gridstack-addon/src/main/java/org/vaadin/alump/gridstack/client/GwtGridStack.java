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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.vaadin.alump.gridstack.client.shared.GridStackChildOptions;
import org.vaadin.alump.gridstack.client.shared.GridStackOptions;

import com.google.gwt.core.client.Duration;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class GwtGridStack extends ComplexPanel {

    private final static Logger LOGGER = Logger.getLogger(GwtGridStack.class.getName());
    private final static int FLUSH_DELAY_MS = 250;

    boolean initialized = false;

    private static int idCounter = 0;
    public final String elementId;

    protected List<GwtGridStackMoveListener> moveListeners = new ArrayList<GwtGridStackMoveListener>();
    protected List<GwtGridStackReadyListener> readyListeners = new ArrayList<GwtGridStackReadyListener>();

    protected long lastEvent = 0;
    protected boolean draggedOrResized = false;

    public final static long DISABLE_CLICK_AFTER_EVENT_MS = 100L;

    public final static String CONTENT_CLASSNAME = "grid-stack-item-content";
    public final static String DRAG_HANDLE_CLASSNAME = GridStackOptions.DRAG_HANDLE_CLASSNAME;
    public final static String DISABLE_SCROLLING_CLASSNAME = "disable-scrolling";
    public final static String INITIALIZING_CLASSNAME = "gridstack-initializing";
    public final static String READY_CLASSNAME = "gridstack-ready";

    private final Map<Element, Widget> widgetWrappers = new HashMap<Element, Widget>();
    private final Map<Widget, GwtGridStackChangedItem> moveQueue = new HashMap<Widget, GwtGridStackChangedItem>();

    public interface GwtGridStackMoveListener {
        void onWidgetsMoved(Map<Widget, GwtGridStackChangedItem> movedChildren);
    }

    public interface GwtGridStackReadyListener {
        void onReady();
    }

	public GwtGridStack() {
        setElement(Document.get().createDivElement());
        setStyleName("grid-stack");

        this.elementId = "gridstack-" + (idCounter++);
        getElement().setId(this.elementId);
	}

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setOptions(final Integer width, final Integer height, final GridStackOptions options) {
        if (!this.initialized) {
            initialize(width, height, GwtGridStackOptions.createFrom(options));
        } else {
            if(options.cellHeight != null) {
                nativeSetCellHeight(options.cellHeight);
            }
            if(options.staticGrid != null) {
                nativeSetGridStatic(options.staticGrid.booleanValue());
            }
        }
    }

    public void initialize(final Integer width, final Integer height, final GwtGridStackOptions options) {
        if (this.initialized) {
            LOGGER.severe("gridstack already initialized");
            return;
        }

        getElement().addClassName(INITIALIZING_CLASSNAME);

        if(width != null) {
            getElement().setAttribute("data-gs-width", width.toString());
        }
        if(height != null) {
            getElement().setAttribute("data-gs-height", height.toString());
        }
        final Duration duration = new Duration();
        initializeGridStack(options);
        LOGGER.info("Initialize grid stack took " + duration.elapsedMillis());
        this.initialized = true;
        getElement().removeClassName(INITIALIZING_CLASSNAME);
        getElement().addClassName(READY_CLASSNAME);

        for (final GwtGridStackReadyListener readyListener : this.readyListeners) {
            readyListener.onReady();
        }
    }

    @Override
    public void add(final Widget widget) {
        add(widget, new GridStackChildOptions());
    }

    public void add(final Widget widget, final GridStackChildOptions info) {
        final Element wrapper = createWrapper(info);
        if (this.initialized) {
            addWidgetWrapperToGridStack(wrapper);
        } else {
            getElement().appendChild(wrapper);
        }

        this.widgetWrappers.put(wrapper, widget);
        super.add(widget, wrapper.getFirstChildElement());
    }

    @Override
    public boolean remove(final Widget widget) {
        if (this.initialized) {
            final Element wrapper = widget.getElement()
                .getParentElement()
                .getParentElement();
            this.widgetWrappers.remove(wrapper);
            removeWidgetWrapperFromGridStack(wrapper);
            wrapper.removeFromParent();
        }
        return super.remove(widget);
    }

    protected Element createWrapper(final GridStackChildOptions info) {
        final Element wrapper = Document.get()
            .createDivElement();
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

        if (info.readOnly) {
            wrapper.setAttribute("data-gs-locked", "yes");
            wrapper.setAttribute("data-gs-no-resize", "yes");
            wrapper.setAttribute("data-gs-no-move", "yes");
        }

        final Element content = Document.get()
            .createDivElement();
        content.addClassName(CONTENT_CLASSNAME);

        if(!info.useDragHandle) {
            content.addClassName(DRAG_HANDLE_CLASSNAME);
        }

        if(info.disableScrolling) {
            wrapper.addClassName(DISABLE_SCROLLING_CLASSNAME);
        }

        if (info.styleName != null) {
            wrapper.addClassName(info.styleName);
        }

        wrapper.appendChild(content);

        if (info.useDragHandle) {
            final Element dragHandle = Document.get()
                .createDivElement();
            dragHandle.addClassName("separate-handle");
            dragHandle.addClassName(DRAG_HANDLE_CLASSNAME);
            wrapper.appendChild(dragHandle);
        }

        return wrapper;
    }

    public Element getWrapper(final Widget child) {
        if(child.getParent() == this) {
           throw new IllegalArgumentException("Given widget is not child of this GridStack");
        }
        return child.getElement().getParentElement();
    }

    protected void onGridStackChange(final Event event, final GwtGridStackChangedItem[] items) {
        // This gets called sometimes with undefined items, not sure why, but ignoring it for now.
        if(items == null) {
            return;
        }

        for(int i = 0; i < items.length; ++i) {
            final GwtGridStackChangedItem item = items[i];
            final Widget child = mapElementToWidget(item.getElement());
            if(child == null) {
                // Null children in list can be ignored?
                continue;
            } else {
                this.moveQueue.put(child, item);
            }
        }

        this.flushMovedTimer.delay();
    }

    protected class FlushMovedTimer extends Timer {
        public void delay() {
            cancel();
            schedule(FLUSH_DELAY_MS);
        }

        @Override
        public void run() {
            for (final GwtGridStackMoveListener moveListener : GwtGridStack.this.moveListeners) {
                moveListener.onWidgetsMoved(GwtGridStack.this.moveQueue);
            }
            GwtGridStack.this.moveQueue.clear();
        }
    }

    private final FlushMovedTimer flushMovedTimer = new FlushMovedTimer();

    protected void onGridStackDragStart(final Event event) {
        updateEventFlag(true);
    }

    protected void onGridStackDragStop(final Event event) {
        updateEventFlag(false);
    }

    protected void onGridStackResizeStart(final Event event) {
        updateEventFlag(true);
    }

    protected void onGridStackResizeStop(final Event event) {
        updateEventFlag(false);
    }

    protected void updateEventFlag(final boolean start) {
        this.draggedOrResized = start;
        this.lastEvent = new Date().getTime();
    }

    protected Widget mapElementToWidget(final Element element) {

        Widget child = this.widgetWrappers.get(element);
        if(child != null) {
            return child;
        }

        final Iterator<Widget> iter = getChildren().iterator();
        while(iter.hasNext()) {
            child = iter.next();
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
            grid.addWidget(element);
        });
    }-*/;

    protected native void removeWidgetWrapperFromGridStack(Element element)
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            grid.removeWidget(element, false);
        });
    }-*/;

    public void addMoveListener(final GwtGridStackMoveListener listener) {
        this.moveListeners.add(listener);
    }

    public void removeMoveListener(final GwtGridStackMoveListener listener) {
        this.moveListeners.remove(listener);
    }

    public void addReadyListener(final GwtGridStackReadyListener listener) {
        this.readyListeners.add(listener);
    }

    public void removeReadyListener(final GwtGridStackReadyListener listener) {
        this.readyListeners.remove(listener);
    }


    public void updateChild(final Widget widget, final GridStackChildOptions options) {
        final Element wrapper = widget.getElement()
            .getParentElement()
            .getParentElement();
        updateWidgetWrapper(wrapper, options.x, options.y, options.width, options.height);
        updateWidgetSizeLimits(wrapper, GwtGridSizeLimits.create(options));
        setLocked(wrapper, options.locked);
        setReadOnly(wrapper, options.readOnly);

        if (options.disableScrolling) {
            wrapper.addClassName(DISABLE_SCROLLING_CLASSNAME);
        } else {
            wrapper.removeClassName(DISABLE_SCROLLING_CLASSNAME);
        }
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
            grid.minWidth(element, values.minWidth);
            //grid.max_width(element, values.maxWidth);
            grid.minHeight(element, values.minHeight);
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

    protected native final void setReadOnly(Element element, boolean readOnly)
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            grid.locked(element, readOnly);
            grid.resizable(element, !readOnly);
            grid.movable(element, !readOnly);
        });
    }-*/;

    public void commit() {
        if (this.initialized && isAttached()) {
            nativeCommit();
        }
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
        if (this.initialized && isAttached()) {
            nativeBatchUpdate();
        }
    }

    protected native final void nativeBatchUpdate()
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            grid.batchUpdate();
        });
    }-*/;

    public boolean isClickOk() {
        return !this.draggedOrResized && new Date().getTime() > (this.lastEvent + DISABLE_CLICK_AFTER_EVENT_MS);
    }

    protected native final void nativeSetGridStatic(boolean gridStatic)
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            grid.setStatic(gridStatic);
        });
    }-*/;

    protected native final void nativeSetCellHeight(String cellHeight)
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            grid.cellHeight(cellHeight);
        });
    }-*/;

    public void redraw() {
        getElement().removeClassName(READY_CLASSNAME);
        nativeRedraw();
        getElement().addClassName(READY_CLASSNAME);
    }

    protected native final void nativeRedraw()
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            grid.commit();
        });
    }-*/;
}