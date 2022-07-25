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

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An application-wide bundle that wraps a {@link ResourceBundle} and can be
 * reloaded.
 * 
 * @author Alexandre DERMONT
 */
public class Bundle {

    /**
     * Listener for reload notifications.
     */
    public static interface BundleListener {
	/**
	 * Called whenever a bundle is reloaded.
	 */
	void onBundleReloaded();
    }

    /** Bundle's name. */
    private String mName;
    /** The bundle's locale. */
    private Locale mLocale;
    /** The underlying java {@link ResourceBundle}. */
    private ResourceBundle mBundle;

    private List<BundleListener> mListeners;

    /**
     * Constructor.
     * 
     * @param name   Bundle's name.
     * @param locale The application's locale.
     */
    public Bundle(String name, Locale locale) {
	mListeners = new CopyOnWriteArrayList<>();
	setName(name);
	setLocale(locale);
	reload();
    }

    /**
     * Reload a bundle, e.g. after the application's language has changed.
     */
    public void reload() {
	mBundle = ResourceBundle.getBundle(mName, mLocale);
	fireBundleReloaded();
    }

    /**
     * Adds a listener for reload events.
     * 
     * @param listener The listener to add.
     */
    public void addBundleListener(BundleListener listener) {
	mListeners.add(listener);
    }

    /**
     * Remove a listener.
     * 
     * @param listener The listener to remove.
     */
    public void removeBundleListener(BundleListener listener) {
	mListeners.remove(listener);
    }

    /**
     * Fires a {@link BundleListener#onBundleReloaded()} event.
     */
    protected void fireBundleReloaded() {
	mListeners.forEach(BundleListener::onBundleReloaded);
    }

    /**
     * Modify the bundle's locale but does not reload the bundle until the
     * {@link #reload()} method is called.
     * 
     * @param locale The new bundle's locale (only the locale's language is used,
     *               not is the country).
     */
    public void setLocale(Locale locale) {
	mLocale = locale;
    }

    /**
     * Set the bundle's name.
     * 
     * @param name The bundle's name.
     */
    public void setName(String name) {
	mName = name;
    }

    /**
     * Get a string from the bundle.
     * 
     * @param key The key.
     * @return The value.
     */
    public String getString(String key) {
	try {
	    return mBundle.getString(key);
	} catch (MissingResourceException e) {
	    return "{key-not-found:" + key + '}';
	}
    }

}
