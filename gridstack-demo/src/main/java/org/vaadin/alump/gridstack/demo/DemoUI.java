package org.vaadin.alump.gridstack.demo;

import com.vaadin.event.LayoutEvents;
import com.vaadin.server.*;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.alump.gridstack.GridStackLayout;

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

        // By default gridstack has three columns
        gridStack = new GridStackLayout(8);
        // See styles.scss of this demo project how to handle columns sizes on CSS size
        gridStack.addStyleName("eight-column-grid-stack");
        gridStack.getOptions().minWidth = 300;
        gridStack.getOptions().verticalMargin = 12;
        gridStack.getOptions().cellHeight = 80;

        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        layout.addComponent(createToolbar());

        Panel gridStackWrapper = new Panel();
        gridStackWrapper.addStyleName("gridstack-wrapper");
        gridStackWrapper.setSizeFull();
        layout.addComponent(gridStackWrapper);
        layout.setExpandRatio(gridStackWrapper, 1f);

        layout.addComponent(new Link(
                "This project is based on gridstack.js JavaScript library, written by Pavel Reznikov",
                new ExternalResource("https://github.com/troolee/gridstack.js")));

        // ----

        gridStackWrapper.setContent(gridStack);
        gridStack.setSizeFull();

        gridStack.addComponent(new Label("Hello World"), 0, 0, 1, 1);

        Component locked = new Label("This is \"locked\" (moving other children will not move this)");
        gridStack.addComponent(locked, 1, 0, 3, 1);
        gridStack.setComponentLocked(locked, true);

        gridStack.addComponent(createForm(), 0, 1, 2, 3);
        gridStack.addComponent(createConsole(), 0, 2, 4, 2);

        Component image = createImage();
        gridStack.addComponent(image, 2, 1, 3, 2);
        gridStack.setComponentSizeLimits(image, 3, null, 2, null);

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
            final CssLayout layout = new CssLayout();
            layout.addStyleName("hep-layout");
            layout.addComponent(new Label("Hep #" + index));
            Button button = new Button("Remove this", ce -> {
                gridStack.removeComponent(layout);
            });
            button.addStyleName(ValoTheme.BUTTON_SMALL);
            button.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
            layout.addComponent(button);
            gridStack.addComponent(layout, -1, -1, 1 + rand.nextInt(3), 1);
        }));

        toolbar.addComponent(createButton(FontAwesome.MINUS, e -> {
            int index = rand.nextInt(gridStack.getComponentCount());
            Iterator<Component> iter = gridStack.iterator();
            for(int i = 0; i < index; ++i) {
                iter.next();
            }
            gridStack.removeComponent(iter.next());
        }));

        CheckBox layoutClicks = new CheckBox("Layout clicks");
        layoutClicks.addValueChangeListener(e -> {
            if((Boolean)e.getProperty().getValue()) {
                gridStack.addLayoutClickListener(layoutClickListener);
            } else {
                gridStack.removeLayoutClickListener(layoutClickListener);
            }
        });
        toolbar.addComponent(layoutClicks);

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
        layout.addStyleName("eventconsole-wrapper");
        layout.setSizeFull();
        eventConsole.setCaption("Event console");
        eventConsole.setSizeFull();
        layout.addComponent(eventConsole);
        return layout;
    }

    private Component createImage() {
        CssLayout wrapper = new CssLayout();
        wrapper.setSizeFull();
        wrapper.addStyleName("image-wrapper");

        Image image = new Image(null, new ThemeResource("images/rude.jpg"));
        wrapper.addComponent(image);
        return wrapper;
    }

    LayoutEvents.LayoutClickListener layoutClickListener = e -> {
        String childHit = e.getChildComponent() != null ? "at child" : "at background";
        addEvent("Layout clicked at " + e.getClientX() + "," + e.getClientY() + " " + childHit);
    };

}
