/**
 * GridStackChildOptions.java (GridStackLayout)
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

import java.io.Serializable;

/**
 * Layouting metadata for child components
 */
public class GridStackChildOptions implements Serializable {
    public int x = 0;
    public int y = 0;
    public int width = 1;
    public int height = 1;
    public boolean autoPosition = false;
    public Integer minWidth = null;
    public Integer minHeight = null;
    public Integer maxWidth = null;
    public Integer maxHeight = null;
    public boolean locked = false;

    public boolean useDragHandle = false;
    public String styleName = null;
}
