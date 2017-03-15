/**
 * GridStackOptions.java (GridStackLayout)
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
 * Properties given to gridstack.js
 * See documentation at https://github.com/troolee/gridstack.js
 * Use null to keep default value of gridstack.js
 */
public class GridStackOptions implements Serializable {

    public final static String DRAG_HANDLE_CLASSNAME = "grid-stack-item-drag-handle";

    public Boolean alwaysShowResizeHandle = null;
    public Boolean animate = null;
    public Boolean auto = null;
    public String cellHeight = null;
    public String handleClass = DRAG_HANDLE_CLASSNAME;
    public Integer height = null;
    public Integer minWidth = null;
    public Boolean staticGrid = null;
    public String verticalMargin = "12px";
    public Integer width = 3;
}
