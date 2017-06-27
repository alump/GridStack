package org.vaadin.alump.gridstack.demo;

import com.ibm.icu.impl.CalendarAstronomer;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.event.LayoutEvents;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.*;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.alump.gridstack.GridStackButton;
import org.vaadin.alump.gridstack.GridStackCoordinates;
import org.vaadin.alump.gridstack.GridStackLayout;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Theme("demo")
@Title("GridStack Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    private Navigator navigator;


    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class,
            widgetset = "org.vaadin.alump.gridstack.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        navigator = new Navigator(this, this);
        navigator.addView(TestView.VIEW_NAME, TestView.class);
        navigator.addView(SplitView.VIEW_NAME, SplitView.class);
    }

}
