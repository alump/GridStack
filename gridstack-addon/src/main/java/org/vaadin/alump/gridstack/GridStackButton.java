package org.vaadin.alump.gridstack;

import com.vaadin.ui.Button;

/**
 * Normal Vaadin Button can not be used inside gridstack without separate drag handle. This button adds workaround to
 * client side that prevents dragging that gets stuck to mouse. Dragging can not be started from this button anyway. If
 * you use separate drag handle, there is no reason to use this component.
 */
public class GridStackButton extends Button {

    public GridStackButton() {
        super();
    }

    public GridStackButton(String caption) {
        super(caption);
    }

    public GridStackButton(String caption, ClickListener listener) {
        super(caption, listener);
    }
}
