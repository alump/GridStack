package org.vaadin.alump.gridstack.demo;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.VerticalLayout;

import java.util.Optional;

/**
 * Created by alump on 14/03/2017.
 */
public class AbstractView extends VerticalLayout implements View {

    private Navigator navigator;

    protected AbstractView() {
        setWidth(100, Unit.PERCENTAGE);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        this.navigator = event.getNavigator();
    }

    protected Optional<Navigator> getNavigator() {
        return Optional.ofNullable(navigator);
    }

    protected void navigateTo(String name) {
        getNavigator().ifPresent(n -> n.navigateTo(name));
    }
}
