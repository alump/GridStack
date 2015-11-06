/**
 * GridStackMoveData.java (GridStackLayout)
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
package org.vaadin.alump.gridstack.client.shared;

import com.vaadin.shared.Connector;

import java.io.Serializable;

/**
 * Data of move event send by client side
 */
public class GridStackMoveData implements Serializable {
    public Connector child;
    public int x;
    public int y;
    public int width;
    public int height;

    public GridStackMoveData() {

    }

    public GridStackMoveData(Connector child, int x, int y, int width, int height) {
        this.child = child;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
