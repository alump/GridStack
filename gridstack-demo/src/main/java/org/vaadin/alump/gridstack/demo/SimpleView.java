package org.vaadin.alump.gridstack.demo;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.alump.gridstack.GridStackLayout;
import org.vaadin.alump.gridstack.GridStackStyling;
import org.vaadin.alump.scaleimage.ScaleImage;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by alump on 14/03/2017.
 */
public class SimpleView extends AbstractView {

    private final static String LOREM_IPSUM_1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do "
        + "eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud "
        + "exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";

    private final static String LOREM_IPSUM_2 = "Duis aute irure dolor in reprehenderit in voluptate velit esse "
        + "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui "
        + "officia deserunt mollit anim id est laborum.";

    public final static String VIEW_NAME = "simple";
    private GridStackLayout gridStack;

    private AtomicInteger childCounter = new AtomicInteger(0);

    public SimpleView() {
        setSizeFull();

        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);
        Button menu = new Button(VaadinIcons.MENU);
        menu.addClickListener(e -> navigateTo(MenuView.VIEW_NAME));
        header.addComponent(menu);
        header.addComponent(new Label("This is simple demo of GridStack add-on"));
        addComponent(header);

        gridStack = new GridStackLayout(3);
        GridStackStyling.applyPapers(gridStack);
        gridStack.addStyleName("simple-gridstack");
        gridStack.setAnimate(true);
        gridStack.setCellHeight("300px");
        gridStack.setVerticalMargin("12px");
        gridStack.setWidth(100, Unit.PERCENTAGE);

        Panel gridStackPanel = new Panel(gridStack);
        gridStackPanel.setSizeFull();
        addComponent(gridStackPanel);
        setExpandRatio(gridStackPanel, 1f);

        initialize();
    }

    private Component createImageChild() {
        String resourceName;
        switch (childCounter.incrementAndGet() % 4) {
            case 1:
                resourceName = "images/goldenbridge2.jpg";
                break;
            case 2:
                resourceName = "images/chavatar.png";
                break;
            case 3:
                resourceName = "images/goldenbridge.jpg";
                break;
            default:
                resourceName = "images/redwood.jpg";
                break;
        }

        ScaleImage image = new ScaleImage(new ThemeResource(resourceName));
        image.addStyleName("simple-cover");
        image.setSizeFull();
        return GridStackStyling.createPaperItemWrapper(image);
    }

    private Component createTextChild() {
        CssLayout wrapper = GridStackStyling.createPaperItemWrapper();
        wrapper.addStyleName("simple-promo");
        wrapper.setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setMargin(true);
        layout.setSpacing(true);
        Label header = new Label("GridStack add-on");
        header.setWidth(100, Unit.PERCENTAGE);
        header.addStyleName("header");
        layout.addComponent(header);
        Label desc = new Label(LOREM_IPSUM_1);
        desc.addStyleName("desc");
        desc.setWidth(100, Unit.PERCENTAGE);
        layout.addComponent(desc);
        Label desc2 = new Label(LOREM_IPSUM_2);
        desc2.addStyleName("desc");
        desc2.setWidth(100, Unit.PERCENTAGE);
        layout.addComponent(desc2);
        wrapper.addComponent(layout);
        return wrapper;
    }

    private void initialize() {
        gridStack.removeAllComponents();
        gridStack.addComponent(createImageChild(), 0, 0, 1, 1, false);

        Component textChild = createTextChild();
        gridStack.addComponent(textChild, 0, 1, 1, 1, false);
        gridStack.setChildItemStyleName(textChild, "yellowish-background");

        gridStack.addComponent(createImageChild(), 1, 0, 1, 2, false);
        gridStack.addComponent(createImageChild(), 0, 2, 2, 1, false);
        gridStack.addComponent(createImageChild(), 2, 0, 1, 3, false);
        gridStack.iterator().forEachRemaining(c -> gridStack.setWrapperScrolling(c, false));
    }

}
