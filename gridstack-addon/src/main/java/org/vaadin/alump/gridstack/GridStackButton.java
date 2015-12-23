/**
 * GridStackButton.java (GridStackLayout)
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

import com.vaadin.ui.Button;

/**
 * Normal Vaadin Button can not be used inside gridstack without separate drag handle. This button adds workaround to
 * client side that prevents dragging that gets stuck to mouse. Dragging can not be started from this button anyway. If
 * you use separate drag handle, there is no reason to use this component.
 */
public class GridStackButton extends Button {

    /**
     * {@inheritDoc}
     */
    public GridStackButton() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public GridStackButton(String caption) {
        super(caption);
    }

    /**
     * {@inheritDoc}
     */
    public GridStackButton(String caption, ClickListener listener) {
        super(caption, listener);
    }
}
