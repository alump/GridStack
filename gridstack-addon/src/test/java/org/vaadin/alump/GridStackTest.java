package org.vaadin.alump;

import com.vaadin.ui.Label;
import junit.framework.Assert;
import org.junit.Test;
import org.vaadin.alump.gridstack.GridStackLayout;
import org.vaadin.alump.gridstack.client.shared.GridStackServerRpc;

// JUnit tests here
public class GridStackTest {

	@Test
	public void thisAlwaysPasses() {
		Assert.assertEquals(true, true);
	}

	@Test
    public void childComponentManagement() {
        GridStackLayout layout = new GridStackLayout(3);
        Assert.assertEquals(0, layout.getComponentCount());
        Label label = new Label("foo");
        layout.addComponent(label, 0, 0);
        Assert.assertEquals(1, layout.getComponentCount());
        Assert.assertEquals(label, layout.getComponent(0, 0));
        layout.removeComponent(label);
        Assert.assertEquals(0, layout.getComponentCount());
    }
}
