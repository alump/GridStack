package org.vaadin.alump.gridstack.client.shared;

import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.AbstractLayoutState;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alump on 30/09/15.
 */
public class GridStackState extends AbstractLayoutState {

    public Map<Connector, GridStackComponentInfo> layoutData = new HashMap<Connector,GridStackComponentInfo>();

    public GridStackOptions gridStackOptions = new GridStackOptions();

}
