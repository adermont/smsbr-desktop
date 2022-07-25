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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.smsbr.desktop.model.Order;
import com.smsbr.desktop.model.OrderBy;

/**
 * SmsbrDesktopApp's preferences.
 * 
 * @author Alexandre DERMONT
 */
public class Preferences {

    private static final Logger logger = System.getLogger(Preferences.class.getName());

    public static final String DEFAULT_PREFERENCES_DIR = ".smsbr-viewer";
    public static final String DEFAULT_PREFERENCES_FILE = "preferences.properties";

    public static final String P_MESSAGE_WIDTH = "preferredMessageWidth";
    public static final String P_ORDER_BY = "preferredOrderBy";
    public static final String P_CONTACT_ORDER = "preferredContactOrder";
    public static final String P_MESSAGE_ORDER = "preferredMessageOrder";
    public static final String P_IMAGE_HEIGHT = "preferredImageHeight";
    public static final String P_LOCALE = "preferredLocale";
    public static final String P_PALETTE = "preferredPalette";
    public static final String P_LAST_FILE = "lastFile";
    public static final String P_LOAD_LAST_FILE = "loadLastFile";
    public static final String P_LANGUAGE = "language";
    public static final String P_EMOJI_SIZE = "emojiSize";

    private String messageWidth;
    private OrderBy orderBy;
    private Order messageOrder;
    private Order contactOrder;
    private int imageHeight;
    private Locale locale;
    private int palette;
    private String lastFile;
    private boolean isLoadLastFile;
    private int preferredEmojiSize;

    private PropertyChangeSupport mPropChangeSupport;

    /**
     * Default constructor.
     */
    public Preferences() {
	mPropChangeSupport = new PropertyChangeSupport(this);
	loadDefaults();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
	mPropChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
	mPropChangeSupport.removePropertyChangeListener(listener);
    }

    public String getPreferredMessageWidth() {
	return messageWidth;
    }

    public OrderBy getPreferredOrder() {
	return orderBy;
    }

    public Order getPreferredMessageOrder() {
	return messageOrder;
    }

    public Order getPreferredContactOrder() {
	return contactOrder;
    }

    public int getPreferredImageHeight() {
	return imageHeight;
    }

    public Locale getPreferredLocale() {
	return locale;
    }

    public int getPreferredPalette() {
	return palette;
    }

    public String getLastFile() {
	return lastFile;
    }

    public boolean isLoadLastFile() {
	return isLoadLastFile;
    }

    public int getPreferredEmojiSize() {
	return preferredEmojiSize;
    }

    public void setPreferredMessageWidth(String s) {
	String oldValue = messageWidth;
	if (s != null && s.matches("[0-9]+(%|px)")) {
	    messageWidth = s;
	} else {
	    messageWidth = "55%";
	}
	if (!Objects.equals(oldValue, messageWidth)) {
	    mPropChangeSupport
		    .firePropertyChange(new PropertyChangeEvent(this, P_MESSAGE_WIDTH, oldValue, messageWidth));
	}
    }

    public void setPreferredOrderBy(OrderBy value) {
	OrderBy oldValue = orderBy;
	if (value != null) {
	    orderBy = value;
	}
	if (!Objects.equals(oldValue, orderBy)) {
	    mPropChangeSupport.firePropertyChange(new PropertyChangeEvent(this, P_ORDER_BY, oldValue, orderBy));
	}
    }

    public void setPreferredMessageOrder(Order value) {
	Order oldValue = messageOrder;
	if (value != null) {
	    messageOrder = value;
	}
	if (!Objects.equals(oldValue, messageOrder)) {
	    mPropChangeSupport
		    .firePropertyChange(new PropertyChangeEvent(this, P_MESSAGE_ORDER, oldValue, messageOrder));
	}
    }

    public void setPreferredContactOrder(Order value) {
	Order oldValue = contactOrder;
	if (value != null) {
	    contactOrder = value;
	}
	if (!Objects.equals(oldValue, contactOrder)) {
	    mPropChangeSupport
		    .firePropertyChange(new PropertyChangeEvent(this, P_CONTACT_ORDER, oldValue, contactOrder));
	}
    }

    public void setPreferredImageHeight(int height) {
	int oldValue = imageHeight;
	if (height > 10 && height < 1000) {
	    imageHeight = height;
	}
	if (!Objects.equals(oldValue, imageHeight)) {
	    mPropChangeSupport.firePropertyChange(new PropertyChangeEvent(this, P_IMAGE_HEIGHT, oldValue, imageHeight));
	}
    }

    public void setPreferredLocale(Locale l) {
	Locale oldValue = locale;
	locale = l;
	if (!Objects.equals(oldValue, locale)) {
	    mPropChangeSupport.firePropertyChange(new PropertyChangeEvent(this, P_LOCALE, oldValue, this.locale));
	}
    }

    public void setPreferredPalette(int palette) {
	int oldValue = this.palette;
	if (palette >= 1 && palette <= 5) {
	    this.palette = palette;
	}
	if (!Objects.equals(oldValue, this.palette)) {
	    mPropChangeSupport.firePropertyChange(new PropertyChangeEvent(this, P_PALETTE, oldValue, palette));
	}
    }

    public void setLastFile(String file) {
	lastFile = file;
    }

    public void setLoadLastFile(boolean loadLastFile) {
	isLoadLastFile = loadLastFile;
    }

    public void setPreferredEmojiSize(int preferredEmojiSize) {
	int oldValue = this.preferredEmojiSize;
	this.preferredEmojiSize = preferredEmojiSize;
	if (!Objects.equals(oldValue, this.preferredEmojiSize)) {
	    mPropChangeSupport
		    .firePropertyChange(new PropertyChangeEvent(this, P_EMOJI_SIZE, oldValue, preferredEmojiSize));
	}
    }

    /**
     * Load the default preferences.
     */
    public void load() {
	File prefsDir = new File(FileUtils.getUserDirectory(), DEFAULT_PREFERENCES_DIR);
	if (!prefsDir.exists()) {
	    prefsDir.mkdirs();
	}

	File prefsFile = new File(prefsDir, DEFAULT_PREFERENCES_FILE);
	if (prefsFile.exists()) {
	    load(prefsFile);
	} else {
	    loadDefaults();
	    save(prefsFile);
	}
    }

    public void save() {
	File prefsDir = new File(FileUtils.getUserDirectory(), DEFAULT_PREFERENCES_DIR);
	File prefsFile = new File(prefsDir, DEFAULT_PREFERENCES_FILE);
	save(prefsFile);
    }

    public void save(File dest) {
	Properties p = new Properties();
	p.setProperty(P_MESSAGE_WIDTH, messageWidth);
	p.setProperty(P_ORDER_BY, orderBy.toString().toUpperCase());
	p.setProperty(P_MESSAGE_ORDER, messageOrder.toString().toUpperCase());
	p.setProperty(P_CONTACT_ORDER, contactOrder.toString().toUpperCase());
	p.setProperty(P_IMAGE_HEIGHT, String.valueOf(imageHeight));
	p.setProperty(P_LOCALE, String.valueOf(locale.getLanguage()));
	p.setProperty(P_PALETTE, String.valueOf(palette));
	p.setProperty(P_LAST_FILE, String.valueOf(lastFile));
	p.setProperty(P_LOAD_LAST_FILE, String.valueOf(isLoadLastFile));
	try (FileOutputStream out = new FileOutputStream(dest);) {
	    p.store(out, null);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void loadDefaults() {
	setPreferredMessageWidth("55%");
	setPreferredOrderBy(OrderBy.DATE);
	setPreferredMessageOrder(Order.ASC);
	setPreferredContactOrder(Order.ASC);
	setPreferredImageHeight(200);
	setPreferredPalette(1);
	setPreferredLocale(Locale.getDefault());
	setLastFile(null);
	setLoadLastFile(false);
	setPreferredEmojiSize(28);
    }

    public void load(File file) {
	Properties p = new Properties();
	try (FileInputStream fis = new FileInputStream(file);) {
	    p.load(fis);
	    setPreferredMessageWidth(p.getProperty(P_MESSAGE_WIDTH, "55%"));
	    setPreferredOrderBy(OrderBy.valueOf(p.getProperty(P_ORDER_BY, OrderBy.DATE.toString()).toUpperCase()));
	    setPreferredMessageOrder(Order.valueOf(p.getProperty(P_MESSAGE_ORDER, Order.ASC.toString()).toUpperCase()));
	    setPreferredContactOrder(Order.valueOf(p.getProperty(P_CONTACT_ORDER, Order.ASC.toString()).toUpperCase()));
	    setPreferredImageHeight(Integer.parseInt(p.getProperty(P_IMAGE_HEIGHT, "200")));
	    setPreferredLocale(new Locale(p.getProperty(P_LOCALE)));
	    setPreferredPalette(Integer.parseInt(p.getProperty(P_PALETTE, "1")));
	    setLastFile(p.getProperty(P_LAST_FILE));
	    setLoadLastFile(Boolean.parseBoolean(p.getProperty(P_LOAD_LAST_FILE, "false")));
	    setPreferredLocale(new Locale(p.getProperty(P_LOCALE, "fr")));
	    setPreferredEmojiSize(Integer.parseInt(p.getProperty(P_EMOJI_SIZE, "28")));

	} catch (Exception e) {
	    logger.log(Level.WARNING, e.getLocalizedMessage(), e);
	    loadDefaults();
	}
    }

    /**
     * @return The preferred font for datetime strings in the contacts list.
     */
    public String getContactListDatetimeFont() {
	return "Consolas";
    }

    /**
     * @return The preferred font for contact's names in the contacts list.
     */
    public String getContactListNameFont() {
	return "Impact";
    }

}
