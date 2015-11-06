/**
 * GridStackMoveEvent.java (GridStackLayout)
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
package org.vaadin.alump.gridstack;


import com.vaadin.ui.Component;

/**
 * Event thrown when child of GridStackLayout has moved and/or resized by user
 */
public class GridStackMoveEvent {

    private final GridStackLayout layout;
    private final Component movedChild;
    private final GridStackCoordinates oldCoordinates;
    private final GridStackCoordinates newCoordinates;


    public interface GridStackMoveListener {
        void onGridStackMove(GridStackMoveEvent event);
    }

    public GridStackMoveEvent(GridStackLayout layout, Component movedChild, GridStackCoordinates oldCoordinates,
                              GridStackCoordinates newCoordinates) {
        this.layout = layout;
        this.movedChild = movedChild;
        this.newCoordinates = newCoordinates;
        this.oldCoordinates = oldCoordinates;
    }

    public GridStackLayout getLayout() {
        return layout;
    }

    public Component getMovedChild() {
        return movedChild;
    }

    public GridStackCoordinates getOld() {
        return oldCoordinates;
    }

    public GridStackCoordinates getNew() {
        return newCoordinates;
    }
}
