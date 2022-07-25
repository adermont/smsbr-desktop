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

import com.smsbr.desktop.app.Bundle;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

/**
 * ComobBox for displaying skins available in the application.
 * 
 * @author Alexandre DERMONT
 */
public class JfxPaletteComboBox extends ComboBox<String> {

    /** SmsbrDesktopApp's bundle for labels. */
    protected Bundle mBundle;

    /**
     * Constructor.
     *
     * @param bundle The bundle to use.
     */
    public JfxPaletteComboBox(Bundle bundle) {
	mBundle = bundle;
	loadPalettes();
	setButtonCell(new CustomPaletteCell());
	setCellFactory(list -> new CustomPaletteCell());
    }

    /**
     * Load available skins.
     */
    private void loadPalettes() {
	getItems().setAll("1", "2", "3", "4", "5");
    }

    /**
     * Custom renderer for ComboBox button and cells.
     */
    private class CustomPaletteCell extends ListCell<String> {
	@Override
	protected void updateItem(String item, boolean empty) {
	    super.updateItem(item, empty);
	    if (empty || item == null) {
		setText(null);
		setGraphic(null);
	    } else {
		int numPalette = Integer.parseInt(item);
		String palettePrefix = mBundle.getString("com.smsbr.desktop.ui.JfxDesktopAppMainPane.palette");
		setText(palettePrefix + " " + numPalette);
		setGraphic(Resources.getPalette(numPalette));
	    }
	}
    }
}
