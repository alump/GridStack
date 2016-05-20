package org.vaadin.alump.gridstack.demo;

import com.vaadin.ui.Component;
import org.vaadin.alump.gridstack.GridStackCoordinates;
import org.vaadin.alump.gridstack.GridStackLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Util class reused in tests
 */
public class GridStackDemoUtil {

    public static Component moveChildToAnotherFreePosition(GridStackLayout gridStack, Component child, int columns, int rows) {
        GridStackCoordinates oldCoords = gridStack.getCoordinates(child);
        int width = oldCoords.getWidth();
        int height = oldCoords.getHeight();

        final int columnsTested = columns;
        final int rowsTested = rows;
        for(int slotIndex = 0; slotIndex < columnsTested * rowsTested; ++slotIndex) {
            int x = slotIndex % columnsTested;
            int y = slotIndex / columnsTested;

            if(oldCoords.isXAndY(x, y)) {
                continue;
            }

            if(gridStack.isAreaEmpty(x, y, width, height)) {
                gridStack.moveComponent(child, x, y);
                return child;
            }
        }

        return null;
    }

    public static void reorderAll(GridStackLayout gridStack, Random rand, int columns, int rows) {
        List<Component> children = new ArrayList<>();
        gridStack.iterator().forEachRemaining(child -> {
            //Move away temporary
            gridStack.moveComponent(child, 100, 100);
            children.add(child);
        });

        Collections.shuffle(children, rand);
        children.forEach(child -> moveChildToAnotherFreePosition(gridStack, child, columns, rows));
    }
}
