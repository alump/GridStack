package org.vaadin.alump.gridstack.demo;

import com.vaadin.navigator.View;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Entry/Menu view for GridStack demo/test application
 */
public class MenuView extends AbstractView {
    public final static String VIEW_NAME = "";

    public MenuView() {
        setMargin(true);
        setSpacing(true);

        Label header = new Label("GridStack Vaadin add-on");
        header.addStyleName(ValoTheme.LABEL_H1);
        addComponent(header);

        addComponents(
                createNavigation(SimpleView.class, SimpleView.VIEW_NAME, "Simple example"),
                createNavigation(TestView.class, TestView.VIEW_NAME, "Test it"),
                createNavigation(SplitView.class, SplitView.VIEW_NAME, "List demo")
        );

        addComponent(new Link(
                "GridStack Vaadin add-on project page on GitHub",
                new ExternalResource("https://github.com/alump/GridStack")));

        addComponent(new Link(
                "This project is based on gridstack.js JavaScript library, written by Pavel Reznikov",
                new ExternalResource("https://github.com/troolee/gridstack.js")));
    }

    private Button createNavigation(Class<? extends View> viewClass, String name, String text) {
        Button button = new Button(text, e -> navigateTo(name));
        button.setWidth(300, Unit.PIXELS);
        return button;
    }
}
