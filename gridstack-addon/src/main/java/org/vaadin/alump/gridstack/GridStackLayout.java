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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.vaadin.alump.gridstack.client.shared.GridStackChildOptions;
import org.vaadin.alump.gridstack.client.shared.GridStackLayoutState;
import org.vaadin.alump.gridstack.client.shared.GridStackMoveData;
import org.vaadin.alump.gridstack.client.shared.GridStackServerRpc;

import com.vaadin.annotations.JavaScript;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.Connector;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Registration;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Component;

/**
 * Vaadin layout using gridstack.js library to layout components
 *
 * gridstack.js by Pavel Reznikov: http://troolee.github.io/gridstack.js/
 */
@JavaScript({"jquery-3.3.1.min.js", "jquery-ui.min.js", "lodash.min.js", "gridstack.js", "gridstack.jQueryUI.js"})
public class GridStackLayout extends AbstractLayout implements LayoutEvents.LayoutClickNotifier {

    protected final List<Component> components = new ArrayList<>();

    private final List<GridStackMoveEvent.GridStackMoveListener> moveListeners = new ArrayList<>();

    private final List<GridStackReadyEvent.GridStackReadyListener> readyListeners = new ArrayList<>();

    private int readyCalls = 0;

    /**
     * Use this as x or y coordinate if you want to leave slot selection of component to client side
     */
    public final static int CLIENT_SIDE_SELECTS = -1;

    private GridStackServerRpc serverRpc = new GridStackServerRpc() {

        @Override
        public void layoutClick(MouseEventDetails mouseEventDetails, Connector connector) {
            fireEvent(LayoutEvents.LayoutClickEvent.createEvent(GridStackLayout.this,
                    mouseEventDetails, connector));
        }

        @Override
        public void onChildrenMoved(List<GridStackMoveData> moves) {
            Collection<GridStackMoveEvent> events = new ArrayList<GridStackMoveEvent>();
            for (GridStackMoveData move : moves) {
                Component childComponent = (Component) move.child;
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

        @Override
        public void onReady(int widthPx) {
            readyCalls++;
            final GridStackReadyEvent event = new GridStackReadyEvent(GridStackLayout.this, readyCalls == 1, widthPx);
            readyListeners.forEach(l -> l.onGridStackReady(event));
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
    protected GridStackLayoutState getState() {
        return (GridStackLayoutState)super.getState();
    }

    @Override
    protected GridStackLayoutState getState(boolean markDirty) {
        return (GridStackLayoutState)super.getState(markDirty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addComponent(Component component) {
        addComponent(component, CLIENT_SIDE_SELECTS, CLIENT_SIDE_SELECTS);
    }

    /**
     * Add component to layout
     * @param component Component added
     * @param useDragHandle true to add component with a separate drag handle, or false to make whole content act as a
     *                      drag handle. Notice that using a separate drag handle is recommended if you component
     *                      is or contains any active components (buttons etc..)
     */
    public void addComponent(Component component, boolean useDragHandle) {
        addComponent(component, CLIENT_SIDE_SELECTS, CLIENT_SIDE_SELECTS, useDragHandle);
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
     * Add component to given slot
     * @param component Component added
     * @param x Slot's X coordinate
     * @param y Slot's Y coordinate
     * @param useDragHandle true to add component with a separate drag handle, or false to make whole content act as a
     *                      drag handle. Notice that using a separate drag handle is recommended if you component
     *                      is or contains any active components (buttons etc..)
     */
    public void addComponent(Component component, int x, int y, boolean useDragHandle) {
        addComponent(component, x, y, 1, 1, useDragHandle);
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
        addComponent(component, x, y, width, height, true);
    }

    /**
     * Add component to given slot, and define it's size
     * @param component Component added
     * @param x Slot's X coordinate (use negative values if position can be defined on client side)
     * @param y Slot's Y coordinate (use negative values if position can be defined on client side)
     * @param width Width of space reserved (in slots)
     * @param height Height of space reserved (in slots)
     * @param useDragHandle true to add component with a separate drag handle, or false to make whole content act as a
     *                      drag handle. Notice that using a separate drag handle is recommended if you component
     *                      is or contains any active components (buttons etc..)
     */
    public void addComponent(Component component, int x, int y, int width, int height, boolean useDragHandle) {
        super.addComponent(component);
        components.add(component);

        GridStackChildOptions info = new GridStackChildOptions();
        info.x = x;
        info.y = y;
        info.width = width;
        info.height = height;
        info.useDragHandle = useDragHandle;
        getState().childOptions.put(component, info);
    }

    /**
     * Reset component's position and allow child side define new position for it.
     * @param component Child component which position is reset
     */
    public void resetComponentPosition(Component component) {
        moveComponent(component, CLIENT_SIDE_SELECTS, CLIENT_SIDE_SELECTS);
    }

    /**
     * Move given child component
     * @param component Child component moved and/or resized
     * @param x When defined component's X value is updated, if null old value is kept
     * @param y When defined component's Y value is updated, if null old value is kept
     * @throws IllegalArgumentException If given value are invalid (eg. component is not child of this layout)
     */
    public void moveComponent(Component component, Integer x, Integer y) throws IllegalArgumentException {
        moveAndResizeComponent(component, x, y, null, null);
    }

    /**
     * Move given child component
     * @param component Child component moved and/or resized
     * @param width When defined component's width is updated, if null old value is kept
     * @param height When defined component's height is updated, if null old value is kept
     * @throws IllegalArgumentException If given value are invalid (eg. component is not child of this layout)
     */
    public void resizeComponent(Component component, Integer width, Integer height) throws IllegalArgumentException {
        moveAndResizeComponent(component, null, null, width, height);
    }

    /**
     * Move and/or resize given child component
     * @param component Child component moved and/or resized
     * @param x When defined component's X value is updated, if null old value is kept
     * @param y When defined component's Y value is updated, if null old value is kept
     * @param width When defined component's width is updated, if null old value is kept
     * @param height When defined component's height is updated, if null old value is kept
     * @throws IllegalArgumentException If given value are invalid (eg. component is not child of this layout, or
     * coordinates are invalid).
     */
    public void moveAndResizeComponent(Component component, Integer x, Integer y, Integer width, Integer height)
            throws IllegalArgumentException {

        if(x != null & width != null && x >= 0 && x + width > getState(false).gridStackOptions.width) {
            throw new IllegalArgumentException("Component would go outside the right edge of layout");
        }

        GridStackChildOptions info = getState().childOptions.get(component);
        if(info == null) {
            throw new IllegalArgumentException("Given component is not child of GridStackLayout");
        }
        if(x != null) {
            info.x = x;
        }
        if(y != null) {
            info.y = y;
        }
        if(width != null) {
            info.width = width;
        }
        if(height != null) {
            info.height = height;
        }
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
        for (Connector connector : getState().childOptions.keySet()) {
            GridStackChildOptions info = getState().childOptions.get(connector);
            if(acceptInsideHit) {
                if(x >= info.x && x < (info.x + info.width) && y >= info.y && y < (info.y + info.height)) {
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int getComponentCount() {
        return components.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Component> iterator() {
        return components.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Registration addLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        return addListener(EventId.LAYOUT_CLICK_EVENT_IDENTIFIER,
                LayoutEvents.LayoutClickEvent.class, listener,
                LayoutEvents.LayoutClickListener.clickMethod);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        removeListener(EventId.LAYOUT_CLICK_EVENT_IDENTIFIER,
                LayoutEvents.LayoutClickEvent.class, listener);
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
     * Add listener for gridstack ready event
     * @param listener Listener added
     */
    public void addGridStackReadyListener(GridStackReadyEvent.GridStackReadyListener listener) {
        readyListeners.add(listener);
    }

    /**
     * Remove listener of GridStack ready event
     * @param listener Listener removed
     */
    public void removeGridStackReadyListener(GridStackReadyEvent.GridStackReadyListener listener) {
        readyListeners.remove(listener);
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

        for (GridStackMoveEvent.GridStackMoveListener listener : moveListeners) {
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
        if(child == null || child.getParent() != this) {
            throw new IllegalArgumentException("Given component is not child of this layout");
        }
        GridStackChildOptions opt = getState(modify).childOptions.get(child);
        if(opt == null) {
            throw new IllegalStateException("Missing child options");
        }
        return opt;
    }

    /**
     * Define if layout is animated when child components are moved
     * @param animate true to animate, false to not animate
     * @return This GridStackLayout for command chaining
     */
    public GridStackLayout setAnimate(boolean animate) {
        getState().gridStackOptions.animate = animate;
        return this;
    }

    /**
     * Check if layout is animated
     * @return true if animated, false if not
     */
    public boolean isAnimate() {
        return getState(false).gridStackOptions.animate;
    }

    /**
     * Set layout static (no dragging of resizing) or dynamic (dragging and resizing allowed)
     * @param staticGrid true to set static (no dragging of resizing), false to set dynamic (dragging and resizing
     *                   allowed)
     * @return This GridStackLayout for command chaining
     */
    public GridStackLayout setStaticGrid(boolean staticGrid) {
        getState().gridStackOptions.staticGrid = staticGrid;
        return this;
    }

    /**
     * Check if layout is in static mode
     * @return true if in static mode, false if not, null if not defined by server side
     */
    public Boolean isStaticGrid() {
        return getState(false).gridStackOptions.staticGrid;
    }


    /**
     * Check if component has specific dragging handle
     * @param child Child component of layout
     * @return true if component has separate dragging handle, false if whole content acts as dragging handle
     */
    public boolean isComponentWithDragHandle(Component child) {
        return getComponentOptions(child, false).useDragHandle;
    }

    /**
     * Define vertical margin between components on GridStack layout. Value is only read when rendered on client side
     * first time, so changing value after that will not have any effect (unless client side is detached).
     * @param marginPx Vertical margin in pixels
     * @return This GridStackLayout for command chaining
     */
    public GridStackLayout setVerticalMargin(int marginPx) {
        return setVerticalMargin(marginPx + "px");
    }

    /**
     * Define vertical margin between components on GridStack layout. Value is only read when rendered on client side
     * first time, so changing value after that will not have any effect (unless client side is detached).
     * @param margin Vertical margin in CSS units (eg. '10px' or '3em')
     * @return This GridStackLayout for command chaining
     */
    public GridStackLayout setVerticalMargin(String margin) {
        getState(true).gridStackOptions.verticalMargin = margin;
        return this;
    }

    /**
     * Get vertical margin between components. Value might be mismatch to actual value used, if changed after client
     * side was last attached.
     * @return Vertical margin in CSS units (eg. '3px' or '0.2em')
     */
    public String getVerticalMargin() {
        return getState(false).gridStackOptions.verticalMargin;
    }

    /**
     * Define height of cell in pixels.
     * @param heightPx Cell height in pixels
     * @return This GridStackLayout for command chaining
     */
    public GridStackLayout setCellHeight(Integer heightPx) {
        return setCellHeight(Objects.requireNonNull(heightPx) + "px");
    }

    /**
     * Define height as CSS unit (eg. '20px' or '3em')
     * @param height Cell height in CSS units
     * @return This GridStackLayout for command chaining
     */
    public GridStackLayout setCellHeight(String height) {
        getState(true).gridStackOptions.cellHeight = height;
        return this;
    }

    /**
     * Get height of cell in CSS units, if not defined will return empty.
     * @return Cell height in CSS units (eg. '30px' or '3em'), empty if gridstack.js default is used
     */
    public Optional<String> getCellHeight() {
        return Optional.ofNullable(getState(false).gridStackOptions.cellHeight);
    }

    /**
     * Set minimal width. If window width is less, grid will be shown in one-column mode. Changing value after
     * it has been attached on client side will not apply until client side is detached and attached.
     * @param minWidthPx Minimal width in pixels
     * @return This GridStackLayout for command chaining
     */
    public GridStackLayout setMinWidth(int minWidthPx) {
        getState(true).gridStackOptions.minWidth = minWidthPx;
        return this;
    }

    /**
     * Get minimal width. If window width is less, grid will be shown in one-column mode. Value might be mismatch to
     * actual value used, if changed after client side was last attached.
     * @return Minimal width in pixels, if null the gridstack.js default is used.
     */
    public Integer getMinWidth() {
        return getState(false).gridStackOptions.minWidth;
    }

    /**
     * Define if wrapper around child should allow vertical scrolling or not
     * @param child Child of layout
     * @param scrolling true to enable vertical scrolling, false to disable it
     * @throws IllegalArgumentException If child not found
     */
    public void setWrapperScrolling(Component child, boolean scrolling) {
        getComponentOptions(child, true).disableScrolling = !scrolling;
    }

    /**
     * Check if wrapper around child allows vertical scrolling or not
     * @param child Child of layout
     * @return true if wrapper allows vertical scrolling, false if wrapper hides vertical overflow
     * @throws IllegalArgumentException If child not found
     */
    public boolean isWrapperScrolling(Component child) {
        return getComponentOptions(child, false).disableScrolling;
    }

    /**
     * Check if given area is empty. Remember that any client side defined positioning not yet reported back to
     * server side will be unknown and so can result work results.
     * @param x Left edge coordinate of area
     * @param x Top edge coordinate of area
     * @param width Width of area in slots
     * @param height Height of area in slots
     * @return true if area is available and valid for use
     * @throws IllegalArgumentException If invalid values given
     */
    public boolean isAreaEmpty(int x, int y, int width, int height) throws IllegalArgumentException {
        return isAreaEmpty(new GridStackCoordinates(x, y, width, height));
    }

    /**
     * Check if given area is empty. Remember that any client side defined positioning not yet reported back to
     * server side will be unknown and so can result work results. Will also return false if area would go outside the
     * right edge.
     * @param coordinates Coordinate area checked (x, y, width, height)
     * @return true if area is available and valid for use
     * @throws IllegalArgumentException If invalid values given
     */
    public boolean isAreaEmpty(GridStackCoordinates coordinates) throws IllegalArgumentException {
        if(coordinates.getX() < 0) {
            throw new IllegalArgumentException("X can not be negative");
        }
        if(coordinates.getY() < 0) {
            throw new IllegalArgumentException("Y can not be negative");
        }
        if(coordinates.getWidth() <= 0) {
            throw new IllegalArgumentException("Width most be larger than zero");
        }
        if(coordinates.getHeight() <= 0) {
            throw new IllegalArgumentException("Height most be larger than zero");
        }

        // If item would drop out of left side, return false
        if(coordinates.getX() + coordinates.getWidth() > getState(false).gridStackOptions.width) {
            return false;
        }

        for(int dx = 0; dx < coordinates.getWidth(); ++dx) {
            for(int dy = 0; dy < coordinates.getHeight(); ++dy) {
                Component occupant = getComponent(coordinates.getX() + dx, coordinates.getY() + dy, true);
                if(occupant != null) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Set style name applied to item wrapper around given child component. Currently this style name is only applied
     * when child is added to layout. So remember to set it right after adding child to layout (before first client
     * update after it).
     * @param child Child component
     * @param styleName Style name applied to item wrapper
     */
    public void setChildItemStyleName(Component child, String styleName) {
        GridStackChildOptions childOptions = getState().childOptions.get(child);
        if(childOptions == null) {
            throw new IllegalArgumentException("Child not found");
        }
        childOptions.styleName = styleName;
    }

    /**
     * Get style name applied to item wrapper of given child component
     * @param child Child component
     * @return Style name applied
     */
    public Optional<String> getChildItemStyleName(Component child) {
        return Optional.ofNullable(getState(false).childOptions.get(child))
                .flatMap(o -> Optional.ofNullable(o.styleName));
    }

    /**
     * Sets read only state to child component. If the child is set to read only it will not be able to be moved, resized or moved by another component
     *
     * @param child - Child component
     * @param readOnly - State of the component
     */
    public void setComponentReadOnly(final Component child, final boolean readOnly) {
        getComponentOptions(child).readOnly = readOnly;
    }

    /**
     * Check if the child component is in read only state
     *
     * @param child - Child component
     * @return true if the component is in read only state
     */
    public boolean isComponentReadOnly(final Component child) {
        return getComponentOptions(child).readOnly;
    }

}
