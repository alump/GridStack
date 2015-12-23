package org.vaadin.alump.gridstack.client;

import com.google.gwt.user.client.Event;
import com.vaadin.client.ui.VButton;

/**
 * Modify VButton to work better with GwtGridStack
 */
public class GwtGridStackButton extends VButton {

    @Override
    public void onBrowserEvent(Event event) {
        if(!isEnabled()) {
            super.onBrowserEvent(event);
        } else {
            super.onBrowserEvent(event);
            if (event.getTypeInt() == Event.ONMOUSEDOWN) {
                if (event.getButton() == Event.BUTTON_LEFT) {
                    event.stopPropagation();
                }
            }
        }
    }
}
