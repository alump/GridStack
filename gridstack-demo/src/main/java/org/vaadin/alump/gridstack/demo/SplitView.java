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

/**
 * Split view test related to issue #9
 */
public class SplitView extends HorizontalSplitPanel implements View {

    public final static String VIEW_NAME = "split";
    private Random rand = new Random(0xDEADBEEF);

    private GridStackLayout gridStack;

    public SplitView() {
        setSizeFull();

        setFirstComponent(createLeftSide());
        setSecondComponent(createRightSide());
        setSplitPosition(150, Unit.PIXELS);
    }

    private Component createLeftSide() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeFull();

        Button reorder = new Button("Reorder", e -> {
            //moveRandomChildToAnotherFreePosition();
            GridStackDemoUtil.reorderAll(gridStack, rand, 1, 30);
        });
        reorder.addStyleName(ValoTheme.BUTTON_SMALL);
        layout.addComponent(reorder);

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

    // Just ugly hack to find suitable slot on server side. This will onlu work if server side has actual location of
    // child component
    private void moveChildToAnotherFreePosition(Component child) {
        GridStackDemoUtil.moveChildToAnotherFreePosition(gridStack, child, 1, 40);
    }
}
