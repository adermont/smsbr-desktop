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
package com.smsbr.desktop.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.smsbr.desktop.util.ImageUtil;

/**
 * This tool connects to '' and extracts base64 bytes of 'img' elements to store
 * emojis as PNG images in the ./src/main/resources/emojis' folder.
 * 
 * @author Alexandre DERMONT
 */
public class EmojiWebExtractorTool {

    static final String URL = "https://unicode.org/emoji/charts/full-emoji-list.html";

    public static void main(String[] args) throws IOException {
	File toDir = new File("./src/main/resources/emojis");
	toDir.mkdirs();

	List<UnicodeEmoji> emojis = new EmojiWebExtractorTool().getEmojis();
	emojis.forEach(element -> element.codes().forEach(code -> {
	    if (!StringUtils.isBlank(element.base64())) {
		try {
		    ImageUtil.exportBase64ToFile(element.base64(), new File(toDir, code + ".png"));
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}));
    }

    public List<UnicodeEmoji> getEmojis() throws IOException {
	final List<UnicodeEmoji> emojis = new ArrayList<>();

	final Document document = Jsoup.connect(URL).userAgent(HttpConnection.DEFAULT_UA).maxBodySize(0).get();
	final Elements trElements = document.select("tr");
	final Iterator<Element> trElementIterator = trElements.listIterator();

	while (trElementIterator.hasNext()) {
	    final Element trElement = trElementIterator.next();

	    final Elements tdNameElements = trElement.select("td[class=name]");
	    final Elements tdCodeElements = trElement.select("td[class=code]");
	    final Elements tdImgElements = trElement.select("td[class=andr]");
	    final Elements imgElements = tdImgElements.select("img[class=imga]");

	    final String name = tdNameElements.text().trim();
	    final String codes = tdCodeElements.text().trim();
	    final String src = imgElements.attr("src").trim();

	    if ((name != null && !name.isEmpty()) && (codes != null && !codes.isEmpty())) {
		String data = "";
		if (src.startsWith("data:image/png;base64,")) {
		    data = src.substring("data:image/png;base64,".length());
		}
		final UnicodeEmoji emoji = new UnicodeEmoji(name, codes, data);
		emojis.add(emoji);
	    }
	}

	return emojis;
    }

    public record UnicodeEmoji(String name, List<String> codes, String base64) {

	public UnicodeEmoji(String name, String codes, String base64) {
	    this(name, toCodes(codes), base64);
	}

	public static List<String> toCodes(String text) {
	    return Arrays.asList(text.replace("U+", "0x").split(" "));
	}

    }
}