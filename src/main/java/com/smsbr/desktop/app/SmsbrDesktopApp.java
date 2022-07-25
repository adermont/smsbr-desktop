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
package com.smsbr.desktop.app;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import com.smsbr.desktop.ui.JfxDesktopAppMainPane;
import com.smsbr.desktop.ui.Resources;

import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * SmsbrDesktopApp for reading "SMS backup & restore" files.
 *
 * @author Alexandre DERMONT
 */
public class SmsbrDesktopApp extends javafx.application.Application {

    public static final String MESSAGES_BUNDLE_NAME = "com.smsbr.desktop.app.messages"; //$NON-NLS-1$
    public static final String VERSION_BUNDLE_NAME = "com.smsbr.desktop.app.version"; //$NON-NLS-1$

    public static final String WINDOW_TITLE = "com.smsbr.desktop.app.SmsbrDesktopApp.windowTitle"; //$NON-NLS-1$

    /**
     * @param args
     */
    public static void main(String[] args) {
	launch(args);
    }

    // ----------------------------------------------------------------

    private Preferences mPreferences;
    private Bundle mBundle;
    private Bundle mVersionBundle;
    private Stage mStage;

    public SmsbrDesktopApp() {
	// Load the user's preferences
	mPreferences = new Preferences();
	mPreferences.load();

	// Get the application's bundles
	mBundle = new Bundle(MESSAGES_BUNDLE_NAME, mPreferences.getPreferredLocale());
	mVersionBundle = new Bundle(VERSION_BUNDLE_NAME, mPreferences.getPreferredLocale());
    }

    /**
     * Set the user's preferences (use this only to bypass default preferences read
     * from user's $homedir).
     * 
     * @param preferences The new preferences to set.
     */
    public void setPreferences(Preferences preferences) {
	mPreferences = preferences;
    }

    /**
     * Show the main application frame.
     * 
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
	mStage = primaryStage;
	JfxDesktopAppMainPane mRoot = new JfxDesktopAppMainPane(mPreferences, mBundle, mVersionBundle);

	mRoot.addLanguageListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
	    ResourceBundle.clearCache();
	    Locale locale = new Locale(newValue);
	    mPreferences.setPreferredLocale(locale);
	    mBundle.setLocale(locale);
	    mBundle.reload();
	});

	primaryStage.setTitle(mBundle.getString(WINDOW_TITLE)); // $NON-NLS-1$
	primaryStage.getIcons().add(Resources.getIcon(Resources.ICON_LOGO_URL, 16).getImage());
	primaryStage.centerOnScreen();
	primaryStage.setScene(new Scene(mRoot, 1000, 600));
	primaryStage.show();

	if (mPreferences.isLoadLastFile()) {
	    File lastFile = new File(mPreferences.getLastFile());
	    if (lastFile.exists()) {
		mRoot.loadFile(lastFile);
	    }
	}
    }

    /**
     * Loads a backup file.
     * 
     * @param file The file to load.
     */
    public void loadFile(File file) {
	if (file.exists()) {
	    ((JfxDesktopAppMainPane) this.mStage.getScene().getRoot()).loadFile(file);
	}
    }

    @Override
    public void stop() throws Exception {
	super.stop();
	mPreferences.save();
    }

}
