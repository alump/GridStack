package org.vaadin.alump.gridstack.demo;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.alump.gridstack.GridStackLayout;
import org.vaadin.alump.gridstack.GridStackStyling;
import org.vaadin.alump.gridstack.demo.openweathermap.CityID;
import org.vaadin.alump.gridstack.demo.openweathermap.OpenWeatherMapEntry;
import org.vaadin.alump.gridstack.demo.openweathermap.OpenWeatherMapQuery;
import org.vaadin.alump.gridstack.demo.openweathermap.WeatherPresentation;
import org.vaadin.alump.scaleimage.ScaleImage;

import java.io.IOException;
import java.util.Iterator;
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

    private final static long DELAY_WEATHER_LOAD_MS = 2000L;

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
        gridStackPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        gridStackPanel.setSizeFull();
        addComponent(gridStackPanel);
        setExpandRatio(gridStackPanel, 1f);

        initialize();
    }

    private void getWeathers(ComponentContainer container) {
        OpenWeatherMapQuery query = new OpenWeatherMapQuery();
        try {
            query.run(r -> {
                Iterator<OpenWeatherMapEntry> iterator = r.getWeathers().iterator();
                container.getUI().access(() -> {
                    container.removeAllComponents();
                    if(iterator.hasNext()) {
                        container.addComponent(new WeatherPresentation(iterator.next()));
                    }
                });
                try {
                    while (iterator.hasNext()) {
                        Thread.sleep(2000);
                        container.getUI().access(() -> {
                            container.addComponent(new WeatherPresentation(iterator.next()));
                        });
                    }
                } catch(InterruptedException e) {

                }
            });
        } catch(IOException e) {
            e.printStackTrace();
            container.getUI().access(() -> {
                container.removeAllComponents();
                Label error = new Label("Sorry, I failed to load content.");
                error.addStyleName("error");
                error.setWidth(100, Unit.PERCENTAGE);
                container.addComponent(error);
            });
        }

    }

    private void delayedWeatherReader(ComponentContainer container) {
        try {
            Thread.sleep(DELAY_WEATHER_LOAD_MS);

            if(!container.isAttached()) {
                return;
            }

            getWeathers(container);

        } catch(InterruptedException e) {
            return;
        }
    }

    private Component createWeatherChild() {
        final CssLayout wrapper = GridStackStyling.createPaperItemWrapper();

        // Because paper styling, this trick is needed to make things scrollable again. As making the component itself
        // to scroll it will break paper look and feel styling.
        CssLayout scrollWrapper = new CssLayout();
        scrollWrapper.setWidth(100, Unit.PERCENTAGE);
        Panel makeScrollable = new Panel(scrollWrapper);
        makeScrollable.setSizeFull();
        makeScrollable.addStyleName(ValoTheme.PANEL_BORDERLESS);
        wrapper.addComponent(makeScrollable);

        Label loading = new Label(
                "Loading weather information (this is made slow by design, to test dynamic changes to content)...");
        loading.setWidth(100, Unit.PERCENTAGE);
        scrollWrapper.addComponent(loading);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        scrollWrapper.addComponent(progressBar);

        Thread thread = new Thread(() -> this.delayedWeatherReader(scrollWrapper));
        thread.start();

        return wrapper;
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
        gridStack.addComponent(createImageChild(), 2, 0, 1, 2, false);

        gridStack.addComponent(createWeatherChild(), 2, 2, 1, 1);

        gridStack.iterator().forEachRemaining(c -> gridStack.setWrapperScrolling(c, false));

    }

}
