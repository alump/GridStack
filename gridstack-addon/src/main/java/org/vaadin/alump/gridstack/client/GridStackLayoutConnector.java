package org.vaadin.alump.gridstack.client;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ui.AbstractLayoutConnector;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;
import org.vaadin.alump.gridstack.client.shared.GridStackState;

@Connect(org.vaadin.alump.gridstack.GridStackLayout.class)
public class GridStackLayoutConnector extends AbstractLayoutConnector {


	public GridStackLayoutConnector() {

	}

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
    }

	@Override
	public GwtGridStack getWidget() {
		return (GwtGridStack) super.getWidget();
	}

	@Override
	public GridStackState getState() {
		return (GridStackState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent event) {
		super.onStateChanged(event);

        if(event.isInitialStateChange() || event.hasPropertyChanged("gridStackProperties")) {
            getWidget().setOptions(GwtGridStackOptions.createFrom(getState().gridStackOptions));
        }
	}

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {

        for (ComponentConnector child : event.getOldChildren()) {
            if (child.getParent() != this) {
                Widget widget = child.getWidget();
                if (widget.isAttached()) {
                    getWidget().remove(widget);
                }
            }
        }

        for (ComponentConnector child : getChildComponents()) {
            if (child.getWidget().getParent() != getWidget()) {
               getWidget().add(child.getWidget(), getState().layoutData.get(child));
            }
        }
    }

    @Override
    public void updateCaption(ComponentConnector componentConnector) {
        //ignore for now
    }
}
