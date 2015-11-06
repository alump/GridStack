package org.vaadin.alump.gridstack.demo;

import com.vaadin.ui.Label;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.alump.gridstack.GridStackLayout;

@Theme("demo")
@Title("GridStack Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI
{

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.alump.gridstack.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        GridStackLayout gridStack = new GridStackLayout();

        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        setContent(layout);

        layout.addComponent(new Label("GridStack Demo"));

        layout.addComponent(gridStack);
        gridStack.setSizeFull();
        layout.setExpandRatio(gridStack, 1f);
        gridStack.addComponent(new Label("Hello World"), 0, 0);
        gridStack.addComponent(new Label("Lorem ipsum"), 1, 0);

    }

}
