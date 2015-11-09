package org.vaadin.alump.gridstack.demo;

import com.vaadin.server.*;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.alump.gridstack.GridStackLayout;
import sun.awt.windows.ThemeReader;

import java.time.Instant;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Theme("demo")
@Title("GridStack Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI
{

    private GridStackLayout gridStack;

    private AtomicInteger eventCounter = new AtomicInteger(0);
    private TextArea eventConsole = new TextArea();

    private Random rand = new Random(0xDEADBEEF);

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.alump.gridstack.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        gridStack = new GridStackLayout();
        //gridStack.getOptions().staticGrid = true;
        gridStack.getOptions().minWidth = 300;
        gridStack.getOptions().verticalMargin = 10;

        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        layout.addComponent(createToolbar());

        layout.addComponent(gridStack);
        gridStack.setSizeFull();
        layout.setExpandRatio(gridStack, 1f);
        gridStack.addComponent(new Label("Hello World"), 0, 0, 1, 1);
        gridStack.addComponent(new Label("Lorem ipsum"), 1, 0, 3, 1);
        gridStack.addComponent(createForm(), 0, 1, 2, 3);
        gridStack.addComponent(createConsole(), 0, 2, 4, 2);

        Component image = createImage();
        gridStack.addComponent(image, 2, 1, 3, 3);
        gridStack.setComponentSizeLimits(image, 3, null, 3, null);

        gridStack.addGridStackMoveListener(events -> {
            final int eventId = eventCounter.getAndIncrement();
            events.stream().forEach(event -> {
                addEvent("event #" + eventId + ": Moved from " + event.getOld().toString() + " to "
                        + event.getNew().toString());
            });
        });
    }

    private Component createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setSpacing(true);

        toolbar.addComponent(new Label("GridStack Demo"));

        toolbar.addComponent(createButton(FontAwesome.PLUS, e -> {
            int index = gridStack.getComponentCount();
            gridStack.addComponent(new Label("Hep #" + index), 0, index - 1, index, 1);
        }));

        toolbar.addComponent(createButton(FontAwesome.MINUS, e -> {
            int index = rand.nextInt(gridStack.getComponentCount());
            Iterator<Component> iter = gridStack.iterator();
            for(int i = 0; i < index; ++i) {
                iter.next();
            }
            gridStack.removeComponent(iter.next());
        }));

        return toolbar;
    }

    private void addEvent(String message) {
        eventConsole.setValue(message + "\r\n" + eventConsole.getValue());
    }

    private Button createButton(Resource icon, Button.ClickListener listener) {
        Button button = new Button();
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        button.addClickListener(listener);
        button.setIcon(icon);
        return button;
    }

    private Component createForm() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        TextField username = new TextField();
        username.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        username.setCaption("Username:");
        layout.addComponent(username);
        PasswordField password = new PasswordField();
        password.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        password.setCaption("Password:");
        layout.addComponent(password);
        Button login = new Button("Login", e-> Notification.show("Logged in?"));
        login.addStyleName(ValoTheme.BUTTON_SMALL);
        layout.addComponent(login);
        return layout;
    }

    private Component createConsole() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        eventConsole.setCaption("Event console");
        eventConsole.setSizeFull();
        layout.addComponent(eventConsole);
        return layout;
    }

    private Component createImage() {
        Image image = new Image(null, new ThemeResource("images/rude.jpg"));
        return image;
    }

}
