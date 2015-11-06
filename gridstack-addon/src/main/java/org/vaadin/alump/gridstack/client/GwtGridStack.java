package org.vaadin.alump.gridstack.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import org.vaadin.alump.gridstack.client.shared.GridStackComponentInfo;
import org.vaadin.alump.gridstack.client.shared.GridStackProperties;

public class GwtGridStack extends ComplexPanel {

    boolean initialized = false;

    private static int idCounter = 0;
    public final String elementId;

	public GwtGridStack() {
        setElement(Document.get().createDivElement());
        setStyleName("grid-stack");

        elementId = "gridstack-" + (idCounter++);
        getElement().setId(elementId);
	}

    public void setProperties(GridStackProperties properties) {
        if(!initialized) {
            initializeGridStack();
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
        getElement().appendChild(wrapper);
        super.add(widget, wrapper.getFirstChildElement());
    }

    @Override
    public boolean remove(Widget widget) {
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

    protected native void initializeGridStack()
    /*-{
        var elementId = this.@org.vaadin.alump.gridstack.client.GwtGridStack::elementId;

        $wnd.$(function () {
            var options = {
                cell_height: 80,
                vertical_margin: 10
            };
            $wnd.$('#' + elementId).gridstack(options);
        });
    }-*/;

}