/**
 * GridStackLayout.java (GridStackLayout)
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
package org.vaadin.alump.gridstack;

import com.vaadin.annotations.JavaScript;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.Connector;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Component;
import org.vaadin.alump.gridstack.client.shared.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Vaadin layout using gridstack.js library to layout components
 *
 * gridstack.js by Pavel Reznikov: http://troolee.github.io/gridstack.js/
 */
@JavaScript({"jquery-1.11.3.min.js", "jquery-ui.min.js", "lodash.min.js", "gridstack.js"})
public class GridStackLayout extends AbstractLayout implements LayoutEvents.LayoutClickNotifier {

    protected final List<Component> components = new ArrayList<Component>();

    private boolean initialClientResponseSent = false;

    private final List<GridStackMoveEvent.GridStackMoveListener> moveListeners = new ArrayList<GridStackMoveEvent.GridStackMoveListener>();

    private GridStackServerRpc serverRpc = new GridStackServerRpc() {

        @Override
        public void layoutClick(MouseEventDetails mouseEventDetails, Connector connector) {
            fireEvent(LayoutEvents.LayoutClickEvent.createEvent(GridStackLayout.this,
                    mouseEventDetails, connector));
        }

        @Override
        public void onChildrenMoved(List<GridStackMoveData> moves) {
            Collection<GridStackMoveEvent> events = new ArrayList<GridStackMoveEvent>();
            for(GridStackMoveData move : moves) {
                Component childComponent = (Component)move.child;
                GridStackCoordinates oldCoordinates = getCoordinates(childComponent);

                GridStackChildOptions info = getState(false).childOptions.get(move.child);
                info.x = move.x;
                info.y = move.y;
                info.width = move.width;
                info.height = move.height;

                if(!oldCoordinates.equals(getCoordinates(childComponent))) {
                    events.add(createMoveEvent(childComponent, oldCoordinates));
                }
            }
            fireMoveEvents(events);
        }
    };

    /**
     * Creates GridStackLayout with default 3 columns
     */
    public GridStackLayout() {
        super();
        registerRpc(serverRpc, GridStackServerRpc.class);
    }

    /**
     * Create GridStackLayout with defined column count.
     * @param columns Number of columns, if more than 8 see documentation (extra SCSS including required)
     */
    public GridStackLayout(int columns) {
        this();
        if(columns <= 0) {
            throw new IllegalArgumentException("Amount of columns can not be 0 or negative");
        }
        addStyleName("with-" + columns + "-columns");
        getState().gridStackOptions.width = columns;
    }

    /**
     * Create GridStackLayout with defined column and row count.
     * @param columns Number of columns, if more than 8 see documentation (extra SCSS including required)
     * @param rows Maxium amount of rows allowed
     */
    public GridStackLayout(int columns, int rows) {
        this(columns);
        if(rows <= 0) {
            throw new IllegalArgumentException("Amount of rows can not be 0 or negative");
        }
        getState().gridStackOptions.height = rows;
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);
        initialClientResponseSent = true;
    }


    @Override
    protected GridStackLayoutState getState() {
        return (GridStackLayoutState)super.getState();
    }

    @Override
    protected GridStackLayoutState getState(boolean markDirty) {
        return (GridStackLayoutState)super.getState(markDirty);
    }

    @Override
    public void addComponent(Component component) {
        addComponent(component, -1, -1);
    }

    /**
     * Add component to given slot
     * @param component Component added
     * @param x Slot's X coordinate
     * @param y Slot's Y coordinate
     */
    public void addComponent(Component component, int x, int y) {
        addComponent(component, x, y, 1, 1);
    }

    /**
     * Add component to given slot, and define it's size
     * @param component Component added
     * @param x Slot's X coordinate (use negative values if position can be defined on client side)
     * @param y Slot's Y coordinate (use negative values if position can be defined on client side)
     * @param width Width of space reserved (in slots)
     * @param height Height of space reserved (in slots)
     */
    public void addComponent(Component component, int x, int y, int width, int height) {
        super.addComponent(component);
        components.add(component);

        GridStackChildOptions info = new GridStackChildOptions();
        info.x = x;
        info.y = y;
        info.width = width;
        info.height = height;
        getState().childOptions.put(component, info);
    }

    /**
     * Get component with given slot coordinate
     * @param x Slot's X coordinate
     * @param y Slot's Y coordinate
     * @return Component at slot, or null if component not found
     */
    public Component getComponent(int x, int y) {
        return getComponent(x, y, false);
    }

    /**
     * Get component with given slot coordinate
     * @param x Slot's X coordinate
     * @param y Slot's Y coordinate
     * @param acceptInsideHit If true also other slots reserved by component are accepted
     * @return Component at slot, or null if component not found
     */
    public Component getComponent(int x, int y, boolean acceptInsideHit) {
        for(Connector connector : getState().childOptions.keySet()) {
            GridStackChildOptions info = getState().childOptions.get(connector);
            if(acceptInsideHit) {
                if(x >= info.x && y < (info.x + info.width) && y >= info.y && y < (info.y + info.width)) {
                    return (Component) connector;
                }
            } else {
                if (info.x == x && info.y == y) {
                    return (Component) connector;
                }
            }
        }
        return null;
    }

    @Override
    public void removeComponent(Component component) {
        getState().childOptions.remove(component);
        components.remove(component);
        super.removeComponent(component);
    }

    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {
        if(oldComponent == newComponent) {
            return;
        }
        if(oldComponent.getParent() != this) {
            throw new IllegalArgumentException("Replacable component not child of this layout");
        }
        GridStackChildOptions oldOptions = getState(false).childOptions.get(oldComponent);
        removeComponent(oldComponent);

        if(newComponent.getParent() == this) {
            removeComponent(newComponent);
        }
        addComponent(newComponent, oldOptions.x, oldOptions.y, oldOptions.width, oldOptions.height);
    }

    @Override
    public int getComponentCount() {
        return components.size();
    }

    @Override
    public Iterator<Component> iterator() {
        return components.iterator();
    }

    @Override
    public void addLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        addListener(EventId.LAYOUT_CLICK_EVENT_IDENTIFIER,
                LayoutEvents.LayoutClickEvent.class, listener,
                LayoutEvents.LayoutClickListener.clickMethod);
    }

    @Override
    @Deprecated
    public void addListener(LayoutEvents.LayoutClickListener layoutClickListener) {
        addLayoutClickListener(layoutClickListener);
    }

    @Override
    public void removeLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        removeListener(EventId.LAYOUT_CLICK_EVENT_IDENTIFIER,
                LayoutEvents.LayoutClickEvent.class, listener);
    }

    @Override
    @Deprecated
    public void removeListener(LayoutEvents.LayoutClickListener layoutClickListener) {
        removeLayoutClickListener(layoutClickListener);
    }

    /**
     * Get access gridstack.js options, for now these can be only modified before layout has been rendered on client
     * side. This API will be removed as soon as cleaner API is ready.
     * @return Access to gridstack.js's grid options
     */
    @Deprecated
    public GridStackOptions getOptions() {
        if(initialClientResponseSent) {
            throw new IllegalStateException("Options can not be modified after initial response has been sent to client");
        }
        return getState().gridStackOptions;
    }

    /**
     * Add listener for component move events
     * @param listener Listener added
     */
    public void addGridStackMoveListener(GridStackMoveEvent.GridStackMoveListener listener) {
        moveListeners.add(listener);
    }

    /**
     * Remove listener of component move events
     * @param listener Listener removed
     */
    public void removeGridStackMoveListener(GridStackMoveEvent.GridStackMoveListener listener) {
        moveListeners.remove(listener);
    }

    /**
     * Get coordinates (X,Y,width,height) of component
     * @param child Child component of layout
     * @return Coordinates (X,Y,width,height) of component
     * @throws IllegalArgumentException If child not found
     */
    public GridStackCoordinates getCoordinates(Component child) {
        GridStackChildOptions opts = getComponentOptions(child, false);
        return new GridStackCoordinates(opts.x, opts.y, opts.width, opts.height);
    }

    protected GridStackMoveEvent createMoveEvent(Component component, GridStackCoordinates oldCoordinates) {
        return new GridStackMoveEvent(this, component, oldCoordinates,
                getCoordinates(component));
    }

    protected void fireMoveEvents(Collection<GridStackMoveEvent> events) {
        if(events.isEmpty()) {
            return;
        }

        for(GridStackMoveEvent.GridStackMoveListener listener : moveListeners) {
            listener.onGridStackMove(events);
        }
    }

    /**
     * Define size limitations to child component. For now some values must be defined before child has been rendered
     * on client side.
     * @param child Child of this layout
     * @param minWidth Mininum width in slots (null is undefined)
     * @param maxWidth Maxium width in slots (null is undefined)
     * @param minHeight Mininum height in slots (null is undefined)
     * @param maxHeight Maximum height in slots (null is undefined)
     */
    public void setComponentSizeLimits(Component child, Integer minWidth, Integer maxWidth, Integer minHeight, Integer maxHeight) {
        GridStackChildOptions childOpts = getComponentOptions(child);
        childOpts.minWidth = minWidth;
        childOpts.maxWidth = maxWidth;
        childOpts.minHeight = minHeight;
        childOpts.maxHeight = maxHeight;
    }

    /**
     * Check if given child is locked (not allowed to move because of other dragged children)
     * @param child Child component of layout
     * @return true if locked, false if not
     * @throws IllegalArgumentException If child not found
     */
    public boolean isComponentLocked(Component child) {
        return getComponentOptions(child, false).locked;
    }

    /**
     * Change locked state of child. Locked children will not be moved away from other dragged children.
     * @param child Child component of layout
     * @param locked true if locked, false if not
     * @throws IllegalArgumentException If child not found
     */
    public void setComponentLocked(Component child, boolean locked) {
        getComponentOptions(child).locked = locked;
    }

    protected GridStackChildOptions getComponentOptions(Component child) {
        return getComponentOptions(child, true, true);
    }

    protected GridStackChildOptions getComponentOptions(Component child, boolean modify) {
        return getComponentOptions(child, modify, true);
    }

    protected GridStackChildOptions getComponentOptions(Component child, boolean modify, boolean throwIfMissing) {
        if(child.getParent() != this) {
            throw new IllegalArgumentException("Given component is not child of this layout");
        }
        GridStackChildOptions opt = getState(modify).childOptions.get(child);
        if(opt == null) {
            throw new IllegalStateException("Missing child options");
        }
        return opt;
    }
}
