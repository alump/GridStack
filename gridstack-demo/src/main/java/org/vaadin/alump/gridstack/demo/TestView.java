package org.vaadin.alump.gridstack.demo;

import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.alump.gridstack.GridStackButton;
import org.vaadin.alump.gridstack.GridStackCoordinates;
import org.vaadin.alump.gridstack.GridStackLayout;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main test/demo view of GridStackLayout
 */
public class TestView extends AbstractView {

    public final static String VIEW_NAME = "test";

    private GridStackLayout gridStack;

    private AtomicInteger eventCounter = new AtomicInteger(0);
    private AtomicInteger itemCounter = new AtomicInteger(0);
    private TextArea eventConsole = new TextArea();

    private Random rand = new Random(0xDEADBEEF);

    private Component locked;

    // This value can be used as x and y when client side can pick the best slot
    private final static int CLIENT_SELECTS = GridStackLayout.CLIENT_SIDE_SELECTS;

    public TestView() {

        setStyleName("demoContentLayout");
        setSizeFull();
        setMargin(true);
        setSpacing(true);

        // By default gridstack has three columns (and calls that only work before client side attachment)
        gridStack = new GridStackLayout(8)
                .setVerticalMargin(12)
                .setMinWidth(300)
                .setAnimate(true);

        // See styles.scss of this demo project how to handle columns sizes on CSS size
        gridStack.addStyleName("eight-column-grid-stack");

        // One cell height is set to 80 pixels
        gridStack.setCellHeight("80px");

        addComponent(createToolbar());

        Panel gridStackWrapper = new Panel();
        gridStackWrapper.addStyleName("gridstack-wrapper");
        gridStackWrapper.setSizeFull();
        addComponent(gridStackWrapper);
        setExpandRatio(gridStackWrapper, 1f);

        // ----

        gridStackWrapper.setContent(gridStack);
        gridStack.setSizeFull();

        gridStack.addComponent(new Label("This child can be dragged without handle. Please use separate handle "
                + "(default mode) when you child component is, or has, an active Vaadin component."), 0, 0, 1, 3, false);

        locked = new Label("This component can be \"locked\" (moving other children will not move this)");
        gridStack.addComponent(locked, 1, 0, 3, 1);

        gridStack.addComponent(createForm(), 0, 5, 2, 3, false);
        gridStack.addComponent(createConsole(), 0, 3, 4, 2);

        Component image = createImage();
        gridStack.addComponent(image, 2, 1, 3, 2);
        gridStack.setWrapperScrolling(image, false);
        gridStack.setComponentSizeLimits(image, 3, null, 2, null);

        gridStack.addGridStackMoveListener(events -> {
            final int eventId = eventCounter.getAndIncrement();
            events.stream().forEach(event -> {
                if(event.getMovedChild() instanceof TestItem) {
                    TestItem item = (TestItem)event.getMovedChild();
                    item.setHeader(event.getNew());
                }
                addEvent("event #" + eventId + ": Moved from " + event.getOld().toString() + " to "
                        + event.getNew().toString());
            });
        });
    }

    private Component createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setSpacing(true);

        toolbar.addComponent(createButton(VaadinIcons.MENU, "Back to menu",
                e -> navigateTo(MenuView.VIEW_NAME)));

        toolbar.addComponent(new Label("GridStack Demo"));

        toolbar.addComponent(createButton(VaadinIcons.PLUS, "Add component", e -> {
            TestItem layout = new TestItem(itemCounter);
            gridStack.addComponent(layout, CLIENT_SELECTS, CLIENT_SELECTS, 1 + rand.nextInt(3), 1);
            layout.addRemoveClickListener(re -> gridStack.removeComponent(layout));
        }));

        toolbar.addComponent(createButton(VaadinIcons.TRASH, "Remove component", e -> {
            if(gridStack.getComponentCount() < 1) {
                Notification.show("Nothing to remove!");
                return;
            }
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
            if(e.getValue()) {
                gridStack.addLayoutClickListener(layoutClickListener);
            } else {
                gridStack.removeLayoutClickListener(layoutClickListener);
            }
        });
        toolbar.addComponent(layoutClicks);

        toolbar.addComponent(createButton(VaadinIcons.ARROWS, "Move random child to new location",
                e -> moveRandomChildToAnotherFreePosition()));

        toolbar.addComponent(createButton(VaadinIcons.RANDOM, "Reorder all items to new order", e -> reorderAll()));

        CheckBox staticGrid = new CheckBox("Static");
        staticGrid.setDescription("If static, dragging and resizing are not allowed by user");
        staticGrid.addValueChangeListener(e -> {
            gridStack.setStaticGrid(e.getValue());
        });
        toolbar.addComponent(staticGrid);

        CheckBox lockItem = new CheckBox("Lock child");
        lockItem.setDescription("Define if item with text \"can be locked\" is locked or not");
        lockItem.addValueChangeListener(e -> {
            gridStack.setComponentLocked(locked, e.getValue());
        });
        toolbar.addComponent(lockItem);

        return toolbar;
    }

    private void addEvent(String message) {
        eventConsole.setValue(message + "\r\n" + eventConsole.getValue());
    }

    private Button createButton(Resource icon, String caption, String description, Button.ClickListener listener) {
        Button button = new Button();
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        button.addClickListener(listener);
        if(icon != null) {
            button.setIcon(icon);
        }
        if(caption != null) {
            button.setCaption(caption);
        }
        if(description != null) {
            button.setDescription(description);
        }
        return button;
    }

    private Button createButton(Resource icon, String description, Button.ClickListener listener) {
        return createButton(icon, null, description, listener);
    }

    private Button createButton(String caption, String description, Button.ClickListener listener) {
        return createButton(null, caption, description, listener);
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
        info.setWidth(100, Unit.PERCENTAGE);
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

    private void reorderAll() {
        addEvent("Reorder all components...");
        GridStackDemoUtil.reorderAll(gridStack, rand, 8, 8);
    }

    private void moveRandomChildToAnotherFreePosition() {
        List<Component> children = new ArrayList<>();
        gridStack.iterator().forEachRemaining(child -> children.add(child));
        if(!children.isEmpty()) {
            addEvent("Move random child to new position...");
            Collections.shuffle(children, rand);
            moveChildToAnotherFreePosition(children.get(0));
        }
    }

    // Just ugly hack to find suitable slot on server side. This will onlu work if server side has actual location of
    // child component
    private void moveChildToAnotherFreePosition(Component child) {
        Component movedComponent = GridStackDemoUtil.moveChildToAnotherFreePosition(gridStack, child, 8, 8);
        if(movedComponent != null) {
            GridStackCoordinates coords = gridStack.getCoordinates(movedComponent);
            addEvent("Moving child server side to new position x:" + coords.getX() + " y:" + coords.getY() + "...");
        } else {
            addEvent("!!! Failed to find new available position for child");
        }
    }
}
