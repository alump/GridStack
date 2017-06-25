package org.vaadin.alump.gridstack;

import com.vaadin.event.SerializableEventListener;

import java.io.Serializable;

/**
 * Event sent when GridStack implementation on client side is initialized and ready. Can use used to improve initial
 * start up performance.
 */
public class GridStackReadyEvent implements Serializable {

    public interface GridStackReadyListener extends SerializableEventListener {
        void onGridStackReady(GridStackReadyEvent layout);
    }

    private final GridStackLayout layout;
    private final int widthPx;
    private final boolean first;

    public GridStackReadyEvent(GridStackLayout layout, boolean first, int widthPx) {
        this.layout = layout;
        this.first = first;
        this.widthPx = widthPx;
    }

    /**
     * Get source of this event
     * @return
     */
    public GridStackLayout getLayout() {
        return layout;
    }

    /**
     * Width of component on client side when initialized
     * @return Width in pixels, or -1 if width could not be resolved.
     */
    public int getWidthPx() {
        return widthPx;
    }

    /**
     * As this event gets fired each time client side initialized it can happen multiple times for same component. This
     * method allows to verify it's first time. This is useful if you add child components when listener is called.
     * @return
     */
    public boolean isFirst() {
        return first;
    }
}
