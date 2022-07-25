/**
 * Copyright 2022 Alexandre DERMONT
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
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
package com.smsbr.desktop.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Utility class for user interactions with DialogPanes, Alert, FileChooser
 * ...etc.
 * 
 * @author Alexandre DERMONT
 */
public class FxUtil {
    public static void alertAndWait(String message) {
	Alert alert = new Alert(AlertType.INFORMATION);
	alert.setHeaderText(message);
	alert.getButtonTypes().setAll(ButtonType.OK);
	alert.showAndWait();
    }
}
