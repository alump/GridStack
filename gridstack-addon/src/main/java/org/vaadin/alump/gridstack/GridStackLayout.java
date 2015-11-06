package org.vaadin.alump.gridstack;

import com.vaadin.annotations.JavaScript;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.Connector;
import com.vaadin.shared.EventId;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Component;
import org.vaadin.alump.gridstack.client.shared.GridStackComponentInfo;
import org.vaadin.alump.gridstack.client.shared.GridStackServerRpc;
import org.vaadin.alump.gridstack.client.shared.GridStackState;

import java.util.ArrayList;
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

    private GridStackServerRpc serverRpc = new GridStackServerRpc() {

    };

    public GridStackLayout() {
        super();
        registerRpc(serverRpc, GridStackServerRpc.class);
    }


    @Override
    protected GridStackState getState() {
        return (GridStackState)super.getState();
    }

    @Override
    protected GridStackState getState(boolean markDirty) {
        return (GridStackState)super.getState(markDirty);
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

        GridStackComponentInfo info = new GridStackComponentInfo();
        info.x = x;
        info.y = y;
        info.width = width;
        info.height = height;
        getState().layoutData.put(component, info);
    }

    public Component getComponent(int x, int y) {
        for(Connector connector : getState().layoutData.keySet()) {
            GridStackComponentInfo info = getState().layoutData.get(connector);
            if(info.x == x && info.y == y) {
                return (Component)connector;
            }
        }
        return null;
    }

    @Override
    public void removeComponent(Component component) {
        getState().layoutData.remove(component);
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
}
