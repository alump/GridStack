package org.vaadin.alump.gridstack.demo;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.alump.gridstack.GridStackLayout;
import org.vaadin.teemu.VaadinIcons;

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

    private Navigator navigator;
    private Random rand = new Random(0xDEADBEEF);
    private AtomicInteger gridStackCreated = new AtomicInteger(0);

    private final static int ORDER_A[] = {28, 17, 4, 22, 2, 15, 20, 29, 24, 9, 12, 14, 10, 11, 13, 5, 16, 1, 18,
            19, 6, 21, 3, 23, 8, 25, 26, 27, 0, 7};

    private final static String ORDER_A_TEXT[] = {"XXVIII", "XVII", "IV", "XXII", "II", "XV", "XX", "XXIX", "XXIV",
            "IX", "XII", "XIV", "X", "XI", "XIII", "V", "XVI", "I", "XVIII", "XIX", "VI", "XXI", "III", "XXIII", "VIII",
            "XXV", "XXVI", "XXVII", "nulla", "VII"};


    private GridStackLayout gridStack;

    public SplitView() {
        setSizeFull();
        addStyleName("test-splitpanel");

        setFirstComponent(createLeftSide());
        setSecondComponent(createRightSide(true));
        setSplitPosition(200, Unit.PIXELS);
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
        shuffleButton.setWidth(100, Unit.PERCENTAGE);
        shuffleButton.addStyleName(ValoTheme.BUTTON_SMALL);
        layout.addComponent(shuffleButton);

        Button orderAButton = new Button("Roman", e -> {
            AtomicInteger atIndex = new AtomicInteger(0);
            gridStack.iterator().forEachRemaining(iter -> {
                gridStack.moveAndResizeComponent(iter, 0, ORDER_A[atIndex.getAndIncrement()], 1, 1);
            });
        });
        orderAButton.setDescription("Order by roman numbers");
        orderAButton.setWidth(100, Unit.PERCENTAGE);
        orderAButton.addStyleName(ValoTheme.BUTTON_SMALL);
        layout.addComponent(orderAButton);

        Button resetButton = new Button("Reset", e -> {
            AtomicInteger x = new AtomicInteger(0);
            gridStack.iterator().forEachRemaining(child -> {
                gridStack.moveComponent(child, 0, x.getAndAdd(gridStack.getCoordinates(child).getHeight()));
            });
        });
        resetButton.setWidth(100, Unit.PERCENTAGE);
        resetButton.addStyleName(ValoTheme.BUTTON_SMALL);
        layout.addComponent(resetButton);

        Button reset2Button = new Button("Slow New", e -> {
            setSecondComponent(createRightSide());
        });
        reset2Button.setWidth(100, Unit.PERCENTAGE);
        reset2Button.addStyleName(ValoTheme.BUTTON_SMALL);
        layout.addComponent(reset2Button);

        Button reset3Button = new Button("Fast New", e -> {
            setSecondComponent(createRightSide(true));
        });
        reset3Button.setWidth(100, Unit.PERCENTAGE);
        reset3Button.addStyleName(ValoTheme.BUTTON_SMALL);
        layout.addComponent(reset3Button);

        Label expandLabel = new Label("");
        expandLabel.setSizeFull();
        layout.addComponent(expandLabel);
        layout.setExpandRatio(expandLabel, 1f);

        Button toTestButton = new Button("Demo", e -> {
            navigator.navigateTo(TestView.VIEW_NAME);
        });
        toTestButton.setDescription("To main demo view");
        toTestButton.setWidth(100, Unit.PERCENTAGE);
        toTestButton.addStyleName(ValoTheme.BUTTON_SMALL);
        layout.addComponent(toTestButton);

        return layout;
    }

    private Component createRightSide() {
        return createRightSide(false);
    }

    private Component createRightSide(boolean delayedAdd) {
        gridStack = new GridStackLayout(1).setCellHeight(60).setAnimate(true);
        gridStack.setSizeFull();

        if(delayedAdd) {
            gridStack.addGridStackReadyListener(event -> {
                System.out.println("Ready signal received: " + event.getWidthPx());
                addItems(event.getLayout());
            });
        } else {
            addItems(gridStack);
        }

        return gridStack;
    }

    private void addItems(GridStackLayout gridStack) {
        int number = gridStackCreated.getAndIncrement();
        int div = number % 3;

        for(int i = 0; i < 30; ++i) {
            CssLayout layout = new CssLayout();
            layout.addStyleName("split-item-layout");
            layout.setSizeFull();

            CssLayout topRow = new CssLayout();
            topRow.addStyleName("top-row");
            topRow.addStyleName("iter-" + div);
            layout.addComponent(topRow);

            Label itemLabel = new Label("Item #" + i);
            itemLabel.setWidthUndefined();
            topRow.addComponent(itemLabel);

            Button testButton = new Button(VaadinIcons.CLOSE);
            testButton.addStyleName(ValoTheme.BUTTON_SMALL);
            testButton.setData(layout);
            testButton.addClickListener(this::onCloseButton);
            topRow.addComponent(testButton);

            Label secondLabel = new Label("Or as Romans say... " + ORDER_A_TEXT[i]);
            secondLabel.addStyleName("second-row");
            layout.addComponent(secondLabel);

            gridStack.addComponent(layout, 0, i);
        }
    }

    private void onCloseButton(Button.ClickEvent event) {
        Component removeComponent = (Component)event.getButton().getData();
        gridStack.removeComponent(removeComponent);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        navigator = event.getNavigator();
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
