/**
 * GwtGridStackButton.java (GridStackLayout)
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

import com.google.gwt.user.client.Event;
import com.vaadin.client.ui.VButton;

/**
 * Modify VButton to work better with GwtGridStack
 */
public class GwtGridStackButton extends VButton {

    @Override
    public void onBrowserEvent(Event event) {
        if(!isEnabled()) {
            super.onBrowserEvent(event);
        } else {
            super.onBrowserEvent(event);
            if (event.getTypeInt() == Event.ONMOUSEDOWN) {
                if (event.getButton() == Event.BUTTON_LEFT) {
                    event.stopPropagation();
                }
            }
        }
    }
}
