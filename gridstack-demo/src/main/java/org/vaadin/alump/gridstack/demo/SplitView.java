package org.vaadin.alump.gridstack.demo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.alump.gridstack.GridStackLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Split view test related to issue #9
 */
public class SplitView extends HorizontalSplitPanel implements View {

    public final static String VIEW_NAME = "split";
    private Random rand = new Random(0xDEADBEEF);

    private GridStackLayout gridStack;

    public SplitView() {
        setSizeFull();
        addStyleName("test-splitpanel");

        setFirstComponent(createLeftSide());
        setSecondComponent(createRightSide());
        setSplitPosition(150, Unit.PIXELS);
    }

    private Component createLeftSide() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeFull();

        Button shuffleButton = new Button("Shuffle", e -> {
            //moveRandomChildToAnotherFreePosition();
            GridStackDemoUtil.reorderAll(gridStack, rand, 1, 30);
        });
        shuffleButton.addStyleName(ValoTheme.BUTTON_SMALL);
        layout.addComponent(shuffleButton);

        Button resetButton = new Button("Reset", e -> {
            AtomicInteger x = new AtomicInteger(0);
            gridStack.iterator().forEachRemaining(child -> {
                gridStack.moveComponent(child, 0, x.getAndAdd(gridStack.getCoordinates(child).getHeight()));
            });
        });
        resetButton.addStyleName(ValoTheme.BUTTON_SMALL);
        layout.addComponent(resetButton);

        return layout;
    }

    private Component createRightSide() {
        gridStack = new GridStackLayout(1).setCellHeight(50).setAnimate(true);
        gridStack.setSizeFull();

        for(int i = 0; i < 30; ++i) {
            Label itemLabel = new Label("Item #" + i);
            gridStack.addComponent(itemLabel, 0, i);
        }
        return gridStack;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    private void moveRandomChildToAnotherFreePosition() {
        List<Component> children = new ArrayList<>();
        gridStack.iterator().forEachRemaining(child -> children.add(child));
        if(!children.isEmpty()) {
            Collections.shuffle(children, rand);
            moveChildToAnotherFreePosition(children.get(0));
        }
    }

    // Just ugly hack to find suitable slot on server side. This will only work if server side has actual location of
    // child component
    private void moveChildToAnotherFreePosition(Component child) {
        GridStackDemoUtil.moveChildToAnotherFreePosition(gridStack, child, 1, 40);
    }
}
