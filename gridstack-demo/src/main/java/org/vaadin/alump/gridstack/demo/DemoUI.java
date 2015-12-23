package org.vaadin.alump.gridstack.demo;

import com.ibm.icu.impl.CalendarAstronomer;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.*;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.alump.gridstack.GridStackButton;
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
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class,
            widgetset = "org.vaadin.alump.gridstack.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        // By default gridstack has three columns (and calls that only work before client side attachment)
        gridStack = new GridStackLayout(8)
                .setVerticalMargin(12)
                .setMinWidth(300);

        // See styles.scss of this demo project how to handle columns sizes on CSS size
        gridStack.addStyleName("eight-column-grid-stack");

        // One cell height is set to 80 pixels
        gridStack.setCellHeight(80);

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

        gridStack.addComponent(new Label("This child can be dragged without handle. Please use separate handle "
            + "(default mode) when you child component is, or has, an active Vaadin component."), 0, 0, 1, 3, false);

        Component locked = new Label("This is \"locked\" (moving other children will not move this)");
        gridStack.addComponent(locked, 1, 0, 3, 1);
        gridStack.setComponentLocked(locked, true);

        gridStack.addComponent(createForm(), 0, 5, 2, 3, false);
        gridStack.addComponent(createConsole(), 0, 3, 4, 2);

        Component image = createImage();
        gridStack.addComponent(image, 2, 1, 3, 2);
        gridStack.setWrapperScrolling(image, false);
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
        layoutClicks.setDescription("Adds layout click listener to GridStackLayout");
        layoutClicks.addValueChangeListener(e -> {
            if((Boolean)e.getProperty().getValue()) {
                gridStack.addLayoutClickListener(layoutClickListener);
            } else {
                gridStack.removeLayoutClickListener(layoutClickListener);
            }
        });
        toolbar.addComponent(layoutClicks);

        CheckBox staticGrid = new CheckBox("Static");
        staticGrid.setDescription("If static, dragging and resizing are not allowed");
        staticGrid.addValueChangeListener(e -> {
            gridStack.setStaticGrid((Boolean)e.getProperty().getValue());
        });
        toolbar.addComponent(staticGrid);

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
        layout.setWidth(100, Unit.PERCENTAGE);
        TextField username = new TextField();
        username.setWidth(100, Unit.PERCENTAGE);
        username.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        username.setCaption("Username:");
        layout.addComponent(username);
        PasswordField password = new PasswordField();
        password.setWidth(100, Unit.PERCENTAGE);
        password.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        password.setCaption("Password:");
        layout.addComponent(password);
        Button login = new GridStackButton("Login", e-> Notification.show("Logged in?"));
        login.addStyleName(ValoTheme.BUTTON_SMALL);
        layout.addComponent(login);
        layout.setComponentAlignment(login, Alignment.BOTTOM_RIGHT);
        Label info = new Label("Also this child can be dragged without handle. GridStackButton used to resolve event "
                + "issues caused by normal Button.");
        info.addStyleName("info-text");
        layout.addComponent(info);
        return layout;
    }

    private Component createConsole() {
        VerticalLayout layout = new VerticalLayout();
        layout.addStyleName("eventconsole-wrapper");
        layout.setSizeFull();
        eventConsole.setCaption("Event console");
        eventConsole.setSizeFull();
        eventConsole.setValue("Events will be written here.");
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
        boolean clickedAtChild = e.getChildComponent() != null;
        StringBuilder sb = new StringBuilder();
        sb.append("User clicked at [")
                .append(e.getClientX())
                .append(",")
                .append(e.getClientY())
                .append("] ");
        sb.append(clickedAtChild ? "at child" : "at background.");

        if(clickedAtChild) {
            sb.append(": ");
            sb.append(gridStack.getCoordinates(e.getChildComponent()).toString());
        }

        addEvent(sb.toString());
    };

}
