package org.vaadin.alump.gridstack.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import org.vaadin.alump.gridstack.client.shared.GridStackComponentInfo;

import java.util.logging.Logger;

public class GwtGridStack extends ComplexPanel {

    private final static Logger LOGGER = Logger.getLogger(GwtGridStack.class.getName());

    boolean initialized = false;

    private static int idCounter = 0;
    public final String elementId;

	public GwtGridStack() {
        setElement(Document.get().createDivElement());
        setStyleName("grid-stack");

        elementId = "gridstack-" + (idCounter++);
        getElement().setId(elementId);
	}

    public void setOptions(GwtGridStackOptions options) {
        if(!initialized) {
            initializeGridStack(options);
            initialized = true;
        } else {
            //TODO
        }
    }

    @Override
    public void add(Widget widget) {
        add(widget, 0, 0, 1, 1);
    }

    public void add(Widget widget, GridStackComponentInfo info) {
        if(info != null) {
            add(widget, info.x, info.y, info.width, info.height);
        } else {
            add(widget);
        }
    }

    public void add(Widget widget, int x, int y, int width, int height) {
        Element wrapper = createWrapper(x, y, width, height);
        if(initialized) {
            addWidgetWrapperToGridStack(wrapper);
        } else {
            getElement().appendChild(wrapper);
        }
        super.add(widget, wrapper.getFirstChildElement());
    }

    @Override
    public boolean remove(Widget widget) {
        if(initialized) {
            removeWidgetWrapperFromGridStack(widget.getElement().getParentElement().getParentElement());
        }
        return super.remove(widget);
    }

    protected Element createWrapper(int x, int y, int width, int height) {
        Element wrapper = Document.get().createDivElement();
        wrapper.addClassName("grid-stack-item");
        wrapper.setAttribute("data-gs-x", Integer.toString(x));
        wrapper.setAttribute("data-gs-y", Integer.toString(y));
        wrapper.setAttribute("data-gs-width", Integer.toString(width));
        wrapper.setAttribute("data-gs-height", Integer.toString(height));

        Element content = Document.get().createDivElement();
        content.addClassName("grid-stack-content");
        wrapper.appendChild(content);

        return wrapper;
    }

    public Element getWrapper(Widget child) {
        if(child.getParent() == this) {
           throw new IllegalArgumentException("Given widget is not child of this GridStack");
        }
        return child.getElement().getParentElement();
    }

    protected void onGridStackChange(JavaScriptObject event, JavaScriptObject items) {
        LOGGER.fine("on grid stack change");
    }

    protected native void initializeGridStack(GwtGridStackOptions options)
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        var that = this;

        $wnd.$(function () {
            var element = $wnd.$('#' + elementId);
            element.gridstack(options);
            element.on('change', function(e, items) {
                that.@org.vaadin.alump.gridstack.client.GwtGridStack::onGridStackChange(*)(e, items);
            });
        });
    }-*/;

    protected native void addWidgetWrapperToGridStack(Element element)
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            grid.add_widget(element);
        });
    }-*/;

    protected native void removeWidgetWrapperFromGridStack(Element element)
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;
        $wnd.$(function () {
            var grid = $wnd.$('#' + elementId).data('gridstack');
            grid.remove_widget(element, false);
        });
    }-*/;
}