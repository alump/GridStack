package org.vaadin.alump.gridstack.demo;

import com.vaadin.annotations.*;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.*;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;

@Theme("demo")
@Title("GridStack Add-on Demo")
@SuppressWarnings("serial")
@Push()
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
        navigator.addView(MenuView.VIEW_NAME, MenuView.class);
        navigator.addView(TestView.VIEW_NAME, TestView.class);
        navigator.addView(SplitView.VIEW_NAME, SplitView.class);
        navigator.addView(SimpleView.VIEW_NAME, SimpleView.class);
    }

}
