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

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.smsbr.desktop.app.Bundle;
import com.smsbr.desktop.app.Preferences;
import com.smsbr.desktop.model.ImagePart;
import com.smsbr.desktop.model.Sms;
import com.smsbr.desktop.ui.JfxDesktopAppMainPane;
import com.smsbr.desktop.ui.Resources;
import com.vdurmont.emoji.EmojiParser;

/**
 * 
 * @author Alexandre DERMONT
 */
public class Htmlizer {
    /** Private logger for this class. */
    private static Logger logger = System.getLogger(Htmlizer.class.getName());
    /** */
    protected static final String PREFIX = JfxDesktopAppMainPane.class.getName();
    /** */
    protected static final String WEBVIEW_ME = PREFIX + ".webView.me";

    /** */
    public static final String IMAGE_LINK_PREFIX = "data:image/png;base64, ";
    /** */
    protected static final String BALLOON_DATE_FORMAT = "dd MMM yyyy HH:mm";

    // =====================================================================

    /** Application's preferences. */
    protected Preferences mPreferences;
    /** Application's bundle. */
    protected Bundle mBundle;

    /**
     * Constructor.
     *
     * @param bundle      Application's bundle.
     * @param preferences Application's preferences.
     */
    public Htmlizer(Bundle bundle, Preferences preferences) {
	mBundle = bundle;
	mPreferences = preferences;
    }

    /**
     * @return The CSS style sheet associated to the selected palette.
     */
    protected String getStylesheetContent(int numPalette) {
	StringBuilder styles = new StringBuilder();
	try (InputStream stream = Resources.class.getResourceAsStream("resources/styles" + numPalette + ".css");) {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
	    while (reader.ready()) {
		styles.append(reader.readLine());
	    }
	} catch (IOException e) {
	    logger.log(Level.WARNING, e.getLocalizedMessage(), e);
	}
	return styles.toString();
    }

    /**
     * @return HTML header of the document with an in-lined style sheet.
     */
    public String getHtmlDocumentHeader(int numPalette) {
	return new StringBuilder("<html>")
		.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">")
		.append("<head><style>").append(getStylesheetContent(numPalette)).append("</style></head>").toString();
    }

    /**
     * Returns <code>true</code> if 'testDate' is a new day compare to 'currentDay'.
     *
     * @param currentDay Current SMS day.
     * @param testDate   The date to compare to <code>currentDay</code> (ie. date of
     *                   the next SMS to display).
     * @return <code>true</code> <code>true</code> if 'testDate' is a new day
     *         compare to 'currentDay'.
     */
    public boolean isNewDay(long currentDay, long testDate) {
	return !DateUtils.isSameDay(new Date(currentDay), new Date(testDate));
    }

    /**
     * Returns a DIV element that contains the date of the next SMS.
     *
     * @param date Date of the next SMS.
     * @return HTML string of the DIV content.
     */
    public String getHtmlDateDivider(long date) {
	StringBuilder sb = new StringBuilder();
	sb.append("<div style=\"");
	sb.append("display: block; ");
	sb.append("font: 14px Impact; ");
	sb.append("border-radius: 12px; ");
	sb.append("float: none; ");
	sb.append("clear: both; ");
	sb.append("padding: 3; ");
	sb.append("margin: 0 0 15 0; ");
	sb.append("text-align: center; ");
	sb.append("\">");
	sb.append(DateFormatUtils.format(date, BALLOON_DATE_FORMAT));
	sb.append("</div>");
	return sb.toString();
    }

    /**
     * Converts a SMS to equivalent HTML code (with CSS style).
     *
     * @param sms The message to convert into HTML.
     * @return SMS content as HTML code (with CSS style).
     */
    public String getHtmlSmsContent(Sms sms) {
	StringBuilder sb = new StringBuilder();

	String meAlign = "right";
	String otherAlign = "left";

	sb.append("<div class=\"").append(sms.isMe() ? "me" : "other").append("\" style=\"");
	sb.append("position: relative;");
	sb.append("float: ").append(sms.isMe() ? meAlign : otherAlign).append(";");
	sb.append("clear:").append(sms.isMe() ? meAlign : otherAlign).append(";");
	sb.append("width: ").append(mPreferences.getPreferredMessageWidth()).append(";");
	sb.append("border-radius: 10px;");
	sb.append("padding: 10px;");
	sb.append("margin: 10px;");
	sb.append("\">");

	// Image MMS
	if (!sms.getImages().isEmpty()) {
	    List<ImagePart> images = sms.getImages();
	    sb.append("<div style=\"text-align: ").append(sms.isMe() ? meAlign : otherAlign).append(";\">");
	    for (ImagePart image : images) {
		Dimension dim = ImageUtil.normalizeImageDimensions(image, mPreferences.getPreferredImageHeight());

		sb.append("<a href=\"#\" onclick=\"alert('image:").append(image.getUniqueId()).append("');\">");
		sb.append("<img id=\"").append(image.getUniqueId()).append("\" width=\"").append(dim.width)
			.append("px\" height=\"").append(dim.height).append("px\" src=\"").append(IMAGE_LINK_PREFIX)
			.append(image.getBase64()).append("\" />&nbsp;");
		sb.append("</a>");
	    }
	    sb.append("</div>");
	    sb.append("<br/>");
	}

	StringBuffer body = new StringBuffer();

	/**
	 * Emojis replacement
	 * 
	 * @see SmsBackupFile for more infos about how emojis are loaded.
	 */
	Pattern patternEmojis = Pattern.compile("emoji\\:\\/\\/([0-9]*);");
	Matcher matcherEmojis = patternEmojis.matcher(sms.getBody());
	while (matcherEmojis.find()) {
	    matcherEmojis.appendReplacement(body, getHtmlElementForHtmlEmojiCode(matcherEmojis.group(1)));
	}
	matcherEmojis.appendTail(body);

	// Hyperlinks
	String newBody = body.toString();
	body = new StringBuffer();
	Pattern patternHyperlinks = Pattern.compile("(http[s]?\\://[\\S]*)[\\s]?");
	Matcher matcherHyperlinks = patternHyperlinks.matcher(newBody);
	while (matcherHyperlinks.find()) {
	    matcherHyperlinks.appendReplacement(body, getHtmlHyperlink(matcherHyperlinks.group(1)));
	}
	matcherHyperlinks.appendTail(body);
	sb.append(body.toString());

	// Hour
	sb.append("<br/>");
	if (sms.isMe()) {
	    sb.append("<span class=\"hour\" style=\"float: left; align: left;\">");
	} else {
	    sb.append("<span class=\"hour\" style=\"float: right; align: right;\">");
	}
	sb.append(sms.isMe() ? mBundle.getString(WEBVIEW_ME) : sms.getContact().getCompleteName()).append(" | ");
	sb.append(DateFormatUtils.format(sms.getDate(), "HH:mm"));
	sb.append("</span>");
	sb.append("</div><p>");

	return sb.toString().replace("\n", "<br>");
    }

    /**
     * @param group
     * @return
     */
    protected String getHtmlHyperlink(String link) {
	StringBuilder sb = new StringBuilder();
	sb.append("<a href=\"").append(link).append("\" target=\"_blank\" onclick=\"alert('link:" + link + "');\">")
		.append(link).append("</a>");
	return sb.toString();
    }

    /**
     * Returns an HTML "&lt;img&gt;" element replacing the native HTML code because
     * the JavaFX WebView component does not display emojis.
     *
     * @param emojiCode number of the emoji native code (without '&#' and ';').
     * @return An &lt;img&gt; element that replaces the native HTML emoji.
     */
    public String getHtmlElementForHtmlEmojiCode(String emojiCode) {

	String unicode = EmojiParser.parseToUnicode(String.format("&#%s;", emojiCode));
	String hexa = StringUtils
		.upperCase(EmojiParser.parseFromUnicode(unicode, emoji -> emoji.getEmoji().getHtmlHexadecimal()));

	hexa = StringUtils.replaceChars(hexa, "&#X;", "");

	String resName = String.format("/emojis/0x%s.png", hexa);
	URI uri = null;
	try {
	    URL resource = getClass().getResource(resName);
	    if (resource != null) {
		uri = resource.toURI();
	    }
	} catch (URISyntaxException e) {
	    logger.log(Level.WARNING, "Resource not found: " + resName, e);
	}

	return String.format(
		"<img style=\"vertical-align: middle; width: %dpx; height: %dpx; display: inline; \" src=\"%s\" />",
		mPreferences.getPreferredEmojiSize(), mPreferences.getPreferredEmojiSize(), uri);
    }
}
