/**
 * GridStackLayoutConnector.java (GridStackLayout)
 *
 * Copyright 2015 Vaadin Ltd, Sami Viitanen <sami.viitanen@vaadin.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.alump.gridstack.client;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.Util;
import com.vaadin.client.ui.AbstractLayoutConnector;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.LayoutClickEventHandler;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.LayoutClickRpc;
import org.vaadin.alump.gridstack.client.shared.GridStackChildOptions;
import org.vaadin.alump.gridstack.client.shared.GridStackMoveData;
import org.vaadin.alump.gridstack.client.shared.GridStackServerRpc;
import org.vaadin.alump.gridstack.client.shared.GridStackLayoutState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

@Connect(org.vaadin.alump.gridstack.GridStackLayoutNoJQuery.class)
public class GridStackLayoutConnector extends AbstractLayoutConnector {

    private transient final static Logger LOGGER = Logger.getLogger(GridStackLayoutConnector.class.getName());

    private final static int CONVERT_NEGATIVE_IN_COMPARE = 10000;

    @Override
    public void init() {
        super.init();
        getWidget().setMoveHandler(new GwtGridStack.GwtGridStackMoveHandler() {
            @Override
            public void onWidgetsMoved(Widget[] widgets, GwtGridStackChangedItem[] data) {
                List<GridStackMoveData> dataSent = new ArrayList<GridStackMoveData>();
                for(int i = 0; i < widgets.length; ++i) {
                    Widget widget = widgets[i];
                    GwtGridStackChangedItem itemData = data[i];
                    dataSent.add(new GridStackMoveData(getChildConnectorForWidget(widget),
                            itemData.getX(), itemData.getY(), itemData.getWidth(), itemData.getHeight()));
                }
                getRpcProxy(GridStackServerRpc.class).onChildrenMoved(dataSent);
            }
        });
    }

    protected ComponentConnector getChildConnectorForWidget(Widget widget) {
        for(ComponentConnector connector : getChildComponents()) {
            if(connector.getWidget() == widget) {
                return connector;
            }
        }
        return null;
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
	public GridStackLayoutState getState() {
		return (GridStackLayoutState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent event) {
		super.onStateChanged(event);
        clickEventHandler.handleEventHandlerRegistration();

        if(event.isInitialStateChange() || event.hasPropertyChanged("gridStackOptions")) {
            getWidget().setOptions(getState().gridStackOptions.width, getState().gridStackOptions.height,
                    getState().gridStackOptions);
        }

        if(getWidget().isInitialized() && event.hasPropertyChanged("childOptions")) {
            getWidget().batchUpdate();
            //for(Connector connector : getChildConnectorsInCoordinateOrder()) {
            for(Connector connector : getState().childOptions.keySet()) {
                Widget widget = ((ComponentConnector)connector).getWidget();
                getWidget().updateChild(widget, getState().childOptions.get(connector));
            }
            getWidget().commit();
        }
	}

    /* Uncomment this if connectors have to updated in coordinate order
    private List<Connector> getChildConnectorsInCoordinateOrder() {
        List<Connector> list = new ArrayList<Connector>();
        for(Connector connector : getState().childOptions.keySet()) {
            list.add(connector);
        }

        Collections.sort(list, childConnectorComparator);

        return list;
    }

    private transient final Comparator<Connector> childConnectorComparator = new Comparator<Connector>() {
        @Override
        public int compare(Connector a, Connector b) {
            GridStackChildOptions aOptions = getState().childOptions.get(a);
            GridStackChildOptions bOptions = getState().childOptions.get(b);

            int aY = aOptions.y;
            if(aY < 0) {
                aY = CONVERT_NEGATIVE_IN_COMPARE;
            }

            int bY = bOptions.y;
            if(bY < 0) {
                bY = CONVERT_NEGATIVE_IN_COMPARE;
            }

            int comp = Integer.compare(aY, bY);
            if(comp == 0) {

                int aX = aOptions.x;
                if(aX < 0) {
                    aX = CONVERT_NEGATIVE_IN_COMPARE;
                }

                int bX = bOptions.x;
                if(bX < 0) {
                    bX = CONVERT_NEGATIVE_IN_COMPARE;
                }

                comp = Integer.compare(aX, bX);
            }

            return comp;
        }
    };
    */

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
               getWidget().add(child.getWidget(), getState().childOptions.get(child));
            }
        }
    }

    @Override
    public void updateCaption(ComponentConnector componentConnector) {
        //ignore for now
    }

    private transient final LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(this) {

        @Override
        protected ComponentConnector getChildComponent(Element element) {
            return Util.getConnectorForElement(getConnection(), getWidget(),
                    element);
        }

        @Override
        protected LayoutClickRpc getLayoutClickRPC() {
            return getRpcProxy(GridStackServerRpc.class);
        };

        @Override
        protected void fireClick(NativeEvent event) {
            // Because of event handling in js library, resize/dragging causes clicks to parent element
            if(getWidget().isClickOk()) {
                super.fireClick(event);
            }
        }
    };
}
