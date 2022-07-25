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

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

/**
 * A ComboBox containing languages and their associated flags.
 * 
 * @author Alexandre DERMONT
 */
public class JfxLanguageComboBox extends ComboBox<String> {

    /**
     * Constructor.
     */
    public JfxLanguageComboBox() {
	loadLanguages();
	setButtonCell(new CustomLanguageCell());
	setCellFactory(list -> new CustomLanguageCell());
	setMaxHeight(32);
    }

    /**
     * Load all the supported languages.
     */
    private void loadLanguages() {

	ResourceBundle b = ResourceBundle.getBundle("com.smsbr.desktop.app.i18n");
	String[] supportedLocales = b.getString("i18n.supportedLanguages").split(",");

	if (supportedLocales == null || supportedLocales.length == 0) {
	    getItems().add(Locale.FRENCH.getLanguage());
	    getItems().add(Locale.ENGLISH.getLanguage());
	} else {
	    for (String supportedLocale : supportedLocales) {
		getItems().add(supportedLocale);
	    }
	}
    }

    /**
     * Custom cell renderer for both the "button" state (when ComboBox is collapsed)
     * and "cell" state (ComboBox is expanded).
     */
    private class CustomLanguageCell extends ListCell<String> {
	@Override
	protected void updateItem(String item, boolean empty) {
	    super.updateItem(item, empty);
	    if (empty || item == null) {
		setText(null);
		setGraphic(null);
	    } else {
		setText(item);
		setGraphic(Resources.getLanguageFlag(item));
	    }
	}
    }
}
