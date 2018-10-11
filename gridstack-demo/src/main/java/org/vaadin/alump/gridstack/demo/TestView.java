package org.vaadin.alump.gridstack.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.vaadin.alump.gridstack.GridStackButton;
import org.vaadin.alump.gridstack.GridStackCoordinates;
import org.vaadin.alump.gridstack.GridStackLayout;

import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Main test/demo view of GridStackLayout
 */
public class TestView extends AbstractView {

    public final static String VIEW_NAME = "test";

    private GridStackLayout gridStack;

    private final AtomicInteger eventCounter = new AtomicInteger(0);
    private final AtomicInteger itemCounter = new AtomicInteger(0);
    private final TextArea eventConsole = new TextArea();

    private final Random rand = new Random(0xDEADBEEF);

    private final Component locked;
    private final Component readOnly;

    // This value can be used as x and y when client side can pick the best slot
    private final static int CLIENT_SELECTS = GridStackLayout.CLIENT_SIDE_SELECTS;

    public TestView() {

        setStyleName("demoContentLayout");
        setSizeFull();
        setMargin(true);
        setSpacing(true);

        // By default gridstack has three columns (and calls that only work before client side attachment)
        this.gridStack = new GridStackLayout(8)
                .setVerticalMargin(12)
                .setMinWidth(300)
                .setAnimate(true);

        // See styles.scss of this demo project how to handle columns sizes on CSS size
        this.gridStack.addStyleName("eight-column-grid-stack");

        // One cell height is set to 80 pixels
        this.gridStack.setCellHeight("80px");

        addComponent(createToolbar());

        final Panel gridStackWrapper = new Panel();
        gridStackWrapper.addStyleName("gridstack-wrapper");
        gridStackWrapper.setSizeFull();
        addComponent(gridStackWrapper);
        setExpandRatio(gridStackWrapper, 1f);

        // ----

        gridStackWrapper.setContent(this.gridStack);
        this.gridStack.setSizeFull();

        final Label label = new Label("This child can be dragged without handle. Please use separate handle "
                + "(default mode) when you child component is, or has, an active Vaadin component.");
        label.setWidth(100, Unit.PERCENTAGE);
        this.gridStack.addComponent(label, 0, 0, 1, 3, false);

        this.locked = new Label("This component can be \"locked\" (moving other children will not move this)");
        this.locked.setWidth(100, Unit.PERCENTAGE);
        this.gridStack.addComponent(this.locked, 1, 0, 3, 1);

        this.readOnly = new Label("This component can be set to read only (moving and resizing is disabled and moving children over will not move this)");
        this.readOnly.setWidth(100, Unit.PERCENTAGE);
        this.gridStack.addComponent(this.readOnly, 1, 4, 3, 1);

        this.gridStack.addComponent(createForm(), 0, 5, 2, 3, false);
        this.gridStack.addComponent(createConsole(), 0, 3, 4, 2);

        final Component image = createImage();
        this.gridStack.addComponent(image, 2, 1, 3, 2);
        this.gridStack.setWrapperScrolling(image, false);
        this.gridStack.setComponentSizeLimits(image, 3, null, 2, null);

        this.gridStack.addGridStackMoveListener(events -> {
            final int eventId = this.eventCounter.getAndIncrement();
            events.stream().forEach(event -> {
                if(event.getMovedChild() instanceof TestItem) {
                        final TestItem item = (TestItem) event.getMovedChild();
                    item.setHeader(event.getNew());
                }
                addEvent("event #" + eventId + ": Moved from " + event.getOld().toString() + " to "
                        + event.getNew().toString());
            });
        });
    }

    private Component createToolbar() {
        final HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setSpacing(true);

        toolbar.addComponent(createButton(VaadinIcons.MENU, "Back to menu",
                e -> navigateTo(MenuView.VIEW_NAME)));

        toolbar.addComponent(new Label("GridStack Demo"));

        toolbar.addComponent(createButton(VaadinIcons.PLUS, "Add component", e -> {
            final TestItem layout = new TestItem(this.itemCounter);
            this.gridStack.addComponent(layout, CLIENT_SELECTS, CLIENT_SELECTS, 1 + this.rand.nextInt(3), 1);
            layout.addRemoveClickListener(re -> this.gridStack.removeComponent(layout));
        }));

        toolbar.addComponent(createButton(VaadinIcons.TRASH, "Remove component", e -> {
            if (this.gridStack.getComponentCount() < 1) {
                Notification.show("Nothing to remove!");
                return;
            }
            final int index = this.rand.nextInt(this.gridStack.getComponentCount());
            final Iterator<Component> iter = this.gridStack.iterator();
            for(int i = 0; i < index; ++i) {
                iter.next();
            }
            this.gridStack.removeComponent(iter.next());
        }));

        final CheckBox layoutClicks = new CheckBox("Layout clicks");
        layoutClicks.setDescription("Adds layout click listener to GridStackLayout");
        layoutClicks.addValueChangeListener(e -> {
            if(e.getValue()) {
                this.gridStack.addLayoutClickListener(this.layoutClickListener);
            } else {
                this.gridStack.removeLayoutClickListener(this.layoutClickListener);
            }
        });
        toolbar.addComponent(layoutClicks);

        toolbar.addComponent(createButton(VaadinIcons.ARROWS, "Move random child to new location",
                e -> moveRandomChildToAnotherFreePosition()));

        toolbar.addComponent(createButton(VaadinIcons.RANDOM, "Reorder all items to new order", e -> reorderAll()));

        final CheckBox staticGrid = new CheckBox("Static");
        staticGrid.setDescription("If static, dragging and resizing are not allowed by user");
        staticGrid.addValueChangeListener(e -> {
            this.gridStack.setStaticGrid(e.getValue());
        });
        toolbar.addComponent(staticGrid);

        final CheckBox lockItem = new CheckBox("Lock child");
        lockItem.setDescription("Define if item with text \"can be locked\" is locked or not");
        lockItem.addValueChangeListener(e -> {
            this.gridStack.setComponentLocked(this.locked, e.getValue());
        });
        toolbar.addComponent(lockItem);

        final CheckBox readOnlyItem = new CheckBox("Read only child");
        readOnlyItem.setDescription("Define if item with text \"Read only\" is read only or not");
        readOnlyItem.addValueChangeListener(e -> {
            this.gridStack.setComponentReadOnly(this.readOnly, e.getValue());
        });
        toolbar.addComponent(readOnlyItem);

        return toolbar;
    }

    private void addEvent(final String message) {
        this.eventConsole.setValue(message + "\r\n" + this.eventConsole.getValue());
    }

    private Button createButton(final Resource icon, final String caption, final String description, final Button.ClickListener listener) {
        final Button button = new Button();
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

    private Button createButton(final Resource icon, final String description, final Button.ClickListener listener) {
        return createButton(icon, null, description, listener);
    }

    private Button createButton(final String caption, final String description, final Button.ClickListener listener) {
        return createButton(null, caption, description, listener);
    }

    private Component createForm() {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setWidth(100, Unit.PERCENTAGE);
        final TextField username = new TextField();
        username.setWidth(100, Unit.PERCENTAGE);
        username.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        username.setCaption("Username:");
        layout.addComponent(username);
        final PasswordField password = new PasswordField();
        password.setWidth(100, Unit.PERCENTAGE);
        password.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        password.setCaption("Password:");
        layout.addComponent(password);
        final Button login = new GridStackButton("Login", e -> Notification.show("Logged in?"));
        login.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        login.addStyleName(ValoTheme.BUTTON_SMALL);
        layout.addComponent(login);
        layout.setComponentAlignment(login, Alignment.BOTTOM_RIGHT);
        final Label info = new Label(
                "Also this child can be dragged without handle. GridStackButton used to resolve event "
                + "issues caused by normal Button.");
        info.setWidth(100, Unit.PERCENTAGE);
        info.addStyleName("info-text");
        layout.addComponent(info);
        return layout;
    }

    private Component createConsole() {
        final VerticalLayout layout = new VerticalLayout();
        layout.addStyleName("eventconsole-wrapper");
        layout.setSizeFull();
        this.eventConsole.setCaption("Event console");
        this.eventConsole.setSizeFull();
        this.eventConsole.setValue("Events will be written here.");
        layout.addComponent(this.eventConsole);
        return layout;
    }

    private Component createImage() {
        final CssLayout wrapper = new CssLayout();
        wrapper.setSizeFull();
        wrapper.addStyleName("image-wrapper");

        final Image image = new Image(null, new ThemeResource("images/rude.jpg"));
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
            sb.append(this.gridStack.getCoordinates(e.getChildComponent())
                .toString());
        }

        addEvent(sb.toString());
    };

    private void reorderAll() {
        addEvent("Reorder all components...");
        GridStackDemoUtil.reorderAll(this.gridStack, this.rand, 8, 8);
    }

    private void moveRandomChildToAnotherFreePosition() {
        final List<Component> children = new ArrayList<>();
        this.gridStack.iterator()
            .forEachRemaining(child -> children.add(child));
        if(!children.isEmpty()) {
            addEvent("Move random child to new position...");
            Collections.shuffle(children, this.rand);
            moveChildToAnotherFreePosition(children.get(0));
        }
    }

    // Just ugly hack to find suitable slot on server side. This will onlu work if server side has actual location of
    // child component
    private void moveChildToAnotherFreePosition(final Component child) {
        final Component movedComponent = GridStackDemoUtil.moveChildToAnotherFreePosition(this.gridStack, child, 8, 8);
        if(movedComponent != null) {
            final GridStackCoordinates coords = this.gridStack.getCoordinates(movedComponent);
            addEvent("Moving child server side to new position x:" + coords.getX() + " y:" + coords.getY() + "...");
        } else {
            addEvent("!!! Failed to find new available position for child");
        }
    }
}
