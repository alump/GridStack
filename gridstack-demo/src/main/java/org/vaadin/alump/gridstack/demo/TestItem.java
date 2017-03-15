package org.vaadin.alump.gridstack.demo;

import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.alump.gridstack.GridStackCoordinates;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple item used in tests
 */
public class TestItem extends CssLayout {

    private final Label header;
    private final Button removeButton;
    private final int index;

    public TestItem(AtomicInteger counter) {
        index = counter.incrementAndGet();
        addStyleName("hep-layout");
        header = new Label("Hep #" + index);
        addComponent(header);
        removeButton = new Button("Remove this");
        removeButton.addStyleName(ValoTheme.BUTTON_SMALL);
        removeButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        addComponent(removeButton);

        setHeader(null);
    }

    public void setHeader(GridStackCoordinates coordinates) {
        String text = "Hep #" + index;
        if (coordinates != null) {
            text += ": " + coordinates.getX() + " " + coordinates.getY();
        }
        header.setValue(text);
    }

    public void addRemoveClickListener(Button.ClickListener listener) {
        removeButton.addClickListener(listener);
    }
}
