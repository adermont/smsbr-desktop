/**
 * Copyright 2022 Alexandre DERMONT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.smsbr.desktop.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

/**
 * Button with optional graphic and action set at construct time.
 * 
 * @author Alexandre DERMONT
 */
public class JfxButtonAction extends Button {

    /**
     * Constructor.
     */
    public JfxButtonAction() {
	super();
    }

    /**
     * Constructor.
     * 
     * @param id   An identifier useful for testing purpose.
     * @param text The button text.
     */
    public JfxButtonAction(String id, String text) {
	super(text);
	setId(id);
    }

    /**
     * Constructor.
     * 
     * @param id   An identifier useful for testing purpose.
     * @param text The button's text. Can be <code>null</code>.
     * @param icon The button's icon. Can be <code>null</code>.
     */
    public JfxButtonAction(String id, String text, ImageView icon) {
	this(id, text);
	if (icon != null) {
	    setGraphic(icon);
	}
    }

    /**
     * Constructor.
     * 
     * @param id      An identifier useful for testing purpose.
     * @param text    The button's text. Can be <code>null</code>.
     * @param icon    The button's icon. Can be <code>null</code>.
     * @param handler The event handler passed to the
     *                {@link #setOnAction(EventHandler)} method.
     */
    public JfxButtonAction(String id, String text, ImageView icon, EventHandler<ActionEvent> handler) {
	this(id, text, icon);
	setOnAction(handler);
    }
}
