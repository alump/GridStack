package org.vaadin.alump.gridstack;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

import java.util.Arrays;
import java.util.Objects;

/**
 * Styling alternatives provided by add-on. Still under development.
 */
public class GridStackStyling {

    public final static String PAPER_GRIDSTACK_LAYOUT_STYLENAME = "papers";
    public final static String PAPER_ITEM_WRAPPER_STYLENAME = "paper-item";

    public static void applyPapers(GridStackLayout gridStack) {
        apply(gridStack, PAPER_GRIDSTACK_LAYOUT_STYLENAME);
    }

    public static void applyPaperItem(Component component) {
        apply(component, PAPER_ITEM_WRAPPER_STYLENAME);
    }

    private static void apply(Component component, String styleName) {
        Objects.requireNonNull(component).addStyleName(styleName);
    }

    public static CssLayout createPaperItemWrapper(Component ... content) {
        CssLayout paperWrapper = new CssLayout();
        applyPaperItem(paperWrapper);
        paperWrapper.setSizeFull();
        Arrays.stream(content).forEach(c -> paperWrapper.addComponent(c));
        return paperWrapper;
    }
}
