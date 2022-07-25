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

import org.apache.commons.lang3.StringUtils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Icons of the application.
 * 
 * @author Alexandre DERMONT
 */
public abstract class Resources {

    /** Icon */
    public static final String ICON_REFRESH_URL = "resources/refresh.png";
    /** Icon */
    public static final String ICON_OPEN_URL = "resources/open.png";
    /** Icon */
    public static final String ICON_EXPORT_URL = "resources/export.png";
    /** Icon */
    public static final String ICON_CLOSE_URL = "resources/power-button.png";
    /** Icon */
    public static final String ICON_PRINT_URL = "resources/printer.png";
    /** Icon */
    public static final String ICON_BACK_URL = "resources/back.png";
    /** Icon */
    public static final String ICON_ABOUT_URL = "resources/about.png";
    /** Icon */
    public static final String ICON_PREFERENCES_URL = "resources/preferences.png";
    /** Icon */
    public static final String ICON_PICTURE_URL = "resources/picture.png";
    /** Icon */
    public static final String ICON_LOGO_URL = "resources/application-logo.png";

    /**
     * Constructor.
     */
    private Resources() {
	// does nothing, just to hide the constructor access.
    }

    /**
     * Get an ImageView from an application icon's URL.
     * 
     * @param url the icon's URL.
     * @return The ImageView of this icon.
     */
    public static ImageView getIcon(String url) {
	return new ImageView(new Image(Resources.class.getResourceAsStream(url)));
    }

    public static ImageView getIcon(String url, double width) {
	ImageView result = new ImageView(new Image(Resources.class.getResourceAsStream(url)));
	result.setPreserveRatio(true);
	result.setFitWidth(width);
	return result;
    }

    public static ImageView getIcon(String url, double width, double height) {
	ImageView result = new ImageView(new Image(Resources.class.getResourceAsStream(url)));
	result.setPreserveRatio(true);
	result.setFitWidth(width);
	result.setFitHeight(height);
	return result;
    }

    public static ImageView getPalette(int numPalette) {
	return new ImageView(new Image(Resources.class.getResourceAsStream("resources/palette" + numPalette + ".png")));
    }

    public static ImageView getLanguageFlag(String language) {
	if (StringUtils.isAllBlank(language)) {
	    language = Locale.FRENCH.getLanguage();
	}
	return new ImageView(new Image(Resources.class.getResourceAsStream("resources/country-" + language + ".png")));
    }

}
