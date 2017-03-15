package org.vaadin.alump.gridstack.demo;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import org.vaadin.alump.gridstack.GridStackLayout;
import org.vaadin.alump.scaleimage.ScaleImage;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by alump on 14/03/2017.
 */
public class SimpleView extends AbstractView {

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

    private Component createChild() {
        String resourceName;
        switch (childCounter.incrementAndGet() % 4) {
            case 1:
                resourceName = "images/goldenbridge.jpg";
                break;
            case 2:
                resourceName = "images/goldenbridge2.jpg";
                break;
            case 3:
                resourceName = "images/chavatar.png";
                break;
            default:
                resourceName = "images/redwood.jpg";
                break;
        }

        ScaleImage image = new ScaleImage(new ThemeResource(resourceName));
        image.addStyleName("simple-cover");
        image.setSizeFull();
        return image;
    }

    private void initialize() {
        gridStack.removeAllComponents();
        gridStack.addComponent(createChild(), 0, 0, 2, 1, false);
        gridStack.addComponent(createChild(), 2, 0, 1, 2, false);
        gridStack.addComponent(createChild(), 0, 1, 1, 1, false);
        gridStack.addComponent(createChild(), 1, 1, 1, 1, false);
        gridStack.addComponent(createChild(), 0, 2, 3, 1, false);
        gridStack.iterator().forEachRemaining(c -> gridStack.setWrapperScrolling(c, false));
    }

}
