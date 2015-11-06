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
                events.add(createMoveEvent(childComponent, oldCoordinates));
            }
            fireMoveEvents(events);
        }
    };

    public GridStackLayout() {
        super();
        registerRpc(serverRpc, GridStackServerRpc.class);
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
    @Deprecated
    public void addComponent(Component component) {
        addComponent(component, 0, 0);
    }

    public void addComponent(Component component, int x, int y) {
        addComponent(component, x, y, 1, 1);
    }

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

    public Component getComponent(int x, int y) {
        for(Connector connector : getState().childOptions.keySet()) {
            GridStackChildOptions info = getState().childOptions.get(connector);
            if(info.x == x && info.y == y) {
                return (Component)connector;
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
    public void replaceComponent(Component component, Component component1) {
        throw new IllegalStateException("Not implemented");
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
     * Get gridstack.js options, for now these can be only modified before layout has been rendered on client side.
     * @return
     */
    public GridStackOptions getOptions() {
        if(initialClientResponseSent) {
            throw new IllegalStateException("Options can not be modified after initial response has been sent to client");
        }
        return getState().gridStackOptions;
    }

    public void addGridStackMoveListener(GridStackMoveEvent.GridStackMoveListener listener) {
        moveListeners.add(listener);
    }

    public void removeGridStackMoveListener(GridStackMoveEvent.GridStackMoveListener listener) {
        moveListeners.remove(listener);
    }

    public GridStackCoordinates getCoordinates(Component child) {
        GridStackChildOptions opts = getState(false).childOptions.get(child);
        if(opts == null) {
            throw new IllegalArgumentException("Given child not found");
        }
        return new GridStackCoordinates(opts.x, opts.y, opts.width, opts.height);
    }

    protected GridStackMoveEvent createMoveEvent(Component component, GridStackCoordinates oldCoordinates) {
        return new GridStackMoveEvent(this, component, oldCoordinates,
                getCoordinates(component));
    }

    protected void fireMoveEvents(Collection<GridStackMoveEvent> events) {
        for(GridStackMoveEvent.GridStackMoveListener listener : moveListeners) {
            listener.onGridStackMove(events);
        }
    }
}
