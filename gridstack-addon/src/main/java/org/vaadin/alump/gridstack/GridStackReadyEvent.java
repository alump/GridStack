package org.vaadin.alump.gridstack;

/**
 * Event sent when GridStack implementation on client side is initialized and ready. Can use used to improve initial
 * start up performance.
 */
public class GridStackReadyEvent {

    public interface GridStackReadyListener {
        void onGridStackReady(GridStackReadyEvent layout);
    }

    private final GridStackLayout layout;
    private final int widthPx;

    public GridStackReadyEvent(GridStackLayout layout, int widthPx) {
        this.layout = layout;
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
}
