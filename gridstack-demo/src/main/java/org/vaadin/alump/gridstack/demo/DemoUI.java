package org.vaadin.alump.gridstack.demo;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.alump.gridstack.GridStackLayout;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Theme("demo")
@Title("GridStack Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI
{

    private GridStackLayout gridStack;

    private AtomicInteger eventCounter = new AtomicInteger(0);

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.alump.gridstack.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        gridStack = new GridStackLayout();
        //gridStack.getOptions().staticGrid = true;
        gridStack.getOptions().minWidth = 300;
        gridStack.getOptions().verticalMargin = 5;

        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setSpacing(true);
        layout.addComponent(toolbar);

        toolbar.addComponent(new Label("GridStack Demo"));
        toolbar.addComponent(createButton(FontAwesome.PLUS, e -> {
            int index = gridStack.getComponentCount();
            gridStack.addComponent(new Label("Hep #" + index), 0, index - 1, index, 1);
        }));

        layout.addComponent(gridStack);
        gridStack.setSizeFull();
        layout.setExpandRatio(gridStack, 1f);
        gridStack.addComponent(new Label("Hello World"), 0, 0, 1, 1);
        gridStack.addComponent(new Label("Lorem ipsum"), 1, 0, 3, 1);

        gridStack.addGridStackMoveListener(events -> {
            final int eventId = eventCounter.getAndIncrement();
            events.stream().forEach(event -> {
                System.out.println("#" + eventId + " Move from " + event.getOld().toString() + " to "
                        + event.getNew().toString());
            });
        });
    }

    private Button createButton(Resource icon, Button.ClickListener listener) {
        Button button = new Button();
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        button.addClickListener(listener);
        button.setIcon(icon);
        return button;
    }

}
