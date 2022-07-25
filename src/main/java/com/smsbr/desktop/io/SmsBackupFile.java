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
package com.smsbr.desktop.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.smsbr.desktop.model.Contact;
import com.smsbr.desktop.model.Conversations;
import com.smsbr.desktop.model.ImagePart;
import com.smsbr.desktop.model.Sms;
import com.smsbr.desktop.services.SmsBackupFileLoadingTaskHandler;
import com.smsbr.desktop.util.ImageUtil;

import javafx.concurrent.Task;
import javafx.scene.image.Image;

/**
 * Represents an XML File exported in the SMS Backup & Restore format.
 * 
 * For more details about the XML format, please visit <a href=
 * "https://www.synctech.com.au/sms-backup-restore/fields-in-xml-backup-files/"/>
 * 
 * You can load an XML file with the
 * {@link #parse(Task, SmsBackupFileLoadingTaskHandler)} method and then you
 * have to handle events with a custom {@link SmsBackupFileLoadingTaskHandler}.
 * 
 * @author Alexandre DERMONT
 */
public class SmsBackupFile extends File {

    private static final long serialVersionUID = -6285417992743930584L;
    private static Logger logger = System.getLogger(SmsBackupFile.class.getName());

    // Elements
    private static final String SPEC_SMSES = "smses";
    private static final String SPEC_SMS = "sms";
    private static final String SPEC_MMS = "mms";

    // sms & mms attributes
    private static final String SPEC_COUNT = "count";
    private static final String SPEC_ADDRESS = "address";
    private static final String SPEC_CONTACTNAME = "contact_name";
    private static final String SPEC_SERVICE_CENTER = "service_center";
    private static final String SPEC_DATE = "date";

    // SMS fields
    private static final String SPEC_TYPE = "type";
    private static final String SPEC_BODY = "body";

    // MMS fields
    private static final String SPEC_MTYPE = "m_type";
    private static final String SPEC_TEXT = "text";
    private static final String SPEC_PART = "part";
    private static final String SPEC_CL = "cl";
    private static final String SPEC_CT = "ct";
    private static final String SPEC_DATA = "data";
    private static final String SPEC_ADDR = "addr";

    // -------------------------------------------------------------------

    private transient SmsBackupFileMetadata metadata;

    /**
     * Constructor.
     * 
     * @see File#File(File, String)
     */
    public SmsBackupFile(File parent, String child) {
	super(parent, child);
	metadata = new SmsBackupFileMetadata(this.length());
    }

    /**
     * Constructor.
     * 
     * @see File#File(String, String)
     */
    public SmsBackupFile(String parent, String child) {
	super(parent, child);
	metadata = new SmsBackupFileMetadata(this.length());
    }

    /**
     * Constructor.
     * 
     * @see File#File(String)
     */
    public SmsBackupFile(String pathname) {
	super(pathname);
	metadata = new SmsBackupFileMetadata(this.length());
    }

    /**
     * Constructor.
     * 
     * @see File#File(URI)
     */
    public SmsBackupFile(URI uri) {
	super(uri);
	metadata = new SmsBackupFileMetadata(this.length());
    }

    /**
     * @param task
     * @param fileHandler
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public void parse(Task<?> task, SmsBackupFileLoadingTaskHandler fileHandler)
	    throws IOException, SAXException, ParserConfigurationException {

	metadata.clear();

	// Parse the file to read metadata only (count "sms" and "mms" items.
	// This allows to provide progress information during loading.
	parseMetadata(fileHandler);

	// Now parse the whole file content
	parseContent(task, fileHandler);
    }

    /**
     * Parse an XML file and collect only metadata.
     * 
     * @param fileHandler The event handler that will be notified when metadata are
     *                    ready.
     * @throws IOException                  In case of I/O error.
     * @throws SAXException                 In case of a file format problem.
     * @throws ParserConfigurationException If the parser has been configured not
     *                                      correctly.
     */
    protected void parseMetadata(SmsBackupFileLoadingTaskHandler fileHandler)
	    throws IOException, SAXException, ParserConfigurationException {

	// Using lambdas avoids evaluating the argument if the log level is filtered
	logger.log(Level.INFO, () -> String.format("Parsing metadata of file '%s'", getAbsolutePath()));

	SAXParserFactory parserFactory = SAXParserFactory.newDefaultInstance();
	SAXParser reader = parserFactory.newSAXParser();

	DefaultHandler metadataHandler = new DefaultHandler() {
	    @Override
	    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (SPEC_SMSES.equalsIgnoreCase(qName)) {
		    for (int iAtt = 0; iAtt < atts.getLength(); iAtt++) {
			String name = atts.getLocalName(iAtt);
			String value = atts.getValue(iAtt);
			if (SPEC_COUNT.equals(name)) {
			    metadata.setMessageCount(Integer.valueOf(value));
			    break;
			}
		    }
		} else if (SPEC_SMS.equalsIgnoreCase(qName) || SPEC_MMS.equalsIgnoreCase(qName)) {

		    String contactName = "";
		    String contactNumber = "";

		    for (int iAtt = 0; iAtt < atts.getLength(); iAtt++) {
			String name = atts.getLocalName(iAtt);
			String value = atts.getValue(iAtt);

			if (SPEC_ADDRESS.equalsIgnoreCase(name)) {
			    contactNumber = value;
			} else if (SPEC_CONTACTNAME.equalsIgnoreCase(name)) {
			    contactName = value;
			}
		    }

		    metadata.addContact(new Contact(normalizePhoneNumber(contactNumber), contactName));
		}
	    }
	};

	try (FileInputStream fis = new FileInputStream(this)) {
	    reader.parse(new InputSource(fis), metadataHandler);
	}

	fileHandler.onMetadataLoaded(metadata);
    }

    /**
     * Normalize the phone number given in parameter in E164 international standard.
     * 
     * @param contactNumber The number to normalize in E164 international standard.
     * @return the normalized number of the input <code>contactNumber</code> if an
     *         error occurred during parse.
     */
    protected String normalizePhoneNumber(String contactNumber) {
	StringBuilder result = new StringBuilder();
	PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

	contactNumber = StringUtils.replace(contactNumber, "~", " ");

	Iterable<PhoneNumberMatch> numbers = phoneNumberUtil.findNumbers(contactNumber,
		Locale.getDefault().getCountry());
	numbers.forEach(m -> {
	    if (phoneNumberUtil.isValidNumber(m.number())) {
		result.append(phoneNumberUtil.format(m.number(), PhoneNumberFormat.E164)).append(',');
	    }
	});
	if (result.length() >= 1) {
	    result.deleteCharAt(result.length() - 1);
	} else {
	    result.append(contactNumber);
	}
	return result.toString();
    }

    /**
     * Read the content of the underlying file.
     * 
     * @param task        The task this method is called from, for progress purpose.
     *                    Can be <code>null</code>.
     * @param fileHandler The handler that will be called each time a message is
     *                    successfully read.
     * @throws IOException                  In case of an I/O error.
     * @throws SAXException                 If the task has been cancelled or if a
     *                                      parsing exception occurs.
     * @throws ParserConfigurationException If the parser is not well configured.
     */
    public void parseContent(Task<?> task, SmsBackupFileLoadingTaskHandler fileHandler)
	    throws IOException, SAXException, ParserConfigurationException {

	logger.log(Level.INFO, () -> String.format("Parsing content of file '%s'", getAbsolutePath()));

	SAXParserFactory parserFactory = SAXParserFactory.newDefaultInstance();
	SAXParser reader = parserFactory.newSAXParser();

	Conversations conversations = new Conversations();
	Deque<Sms> stack = new ArrayDeque<>();

	// COMPLETELY LOAD THE FILE AS A STRING (very cost effective)
	String content = FileUtils.readFileToString(this, StandardCharsets.UTF_8);
	DefaultHandler contentHandler = new DefaultHandler() {

	    @Override
	    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

		if (task != null && task.isCancelled()) {
		    // The only way to stop the loading process if the task has been cancelled
		    throw new SAXException("Loading task cancelled");
		}
		if (SPEC_SMS.equalsIgnoreCase(qName)) {
		    parseSms(stack, atts);
		} else if (SPEC_MMS.equalsIgnoreCase(qName)) {
		    parseMms(stack, atts);
		} else if (SPEC_PART.equalsIgnoreCase(qName)) {
		    parseMmsPart(stack, atts);
		} else if (SPEC_ADDR.equalsIgnoreCase(qName)) {
		    parseMmsAddr(stack, atts);
		}
	    }

	    @Override
	    public void endElement(String uri, String localName, String qName) throws SAXException {

		if (task != null && task.isCancelled()) {
		    throw new SAXException("Loading task cancelled");
		}

		if (SPEC_SMS.equalsIgnoreCase(qName) || SPEC_MMS.equalsIgnoreCase(qName)) {
		    Sms sms = stack.pop();
		    Contact contact = sms.getContact();

		    if (StringUtils.isAnyBlank(contact.getCompleteName(), contact.getCompleteName())) {
			sms.setDraft(true);
			contact.setCompleteName("");
			contact.setPhoneNumber("");
		    }
		    conversations.add(sms);
		    metadata.addContact(contact);
		    fileHandler.onMessageLoaded(sms);
		}
	    }
	};

	// Replace "&#xxxxxx;" HTML codes by a custom standardized URL like
	// "emoji://xxxxxxx;" that will be more easily converted thereafter.
	StringBuffer sb = new StringBuffer(content.length());
	Pattern pattern = Pattern.compile("\\&\\#([0-9][0-9][0-9]+)\\;");
	Matcher matcher = pattern.matcher(content);
	while (matcher.find()) {
	    matcher.appendReplacement(sb, "emoji://" + matcher.group(1) + ";");
	}
	matcher.appendTail(sb);

	reader.parse(new InputSource(new StringReader(sb.toString())), contentHandler);
    }

    /**
     * Parse a "&lt;sms&gt;" element and make it a {@link Sms} model object. The
     * {@link Sms} object is then enqueued onto the <code>stack</code> parameter.
     * 
     * @param stack The stack to fill with read data.
     * @param atts  The element's attributes.
     */
    private void parseSms(Deque<Sms> stack, Attributes atts) {

	String contactName = "";
	String contactNumber = "";
	String serviceNumber = "";
	long date = 0L;
	String body = "";
	boolean isMe = false;
	boolean isDraft = false;

	for (int iAtt = 0; iAtt < atts.getLength(); iAtt++) {
	    String name = atts.getLocalName(iAtt);
	    String value = atts.getValue(iAtt);

	    if (SPEC_ADDRESS.equalsIgnoreCase(name)) {
		contactNumber = value;
	    } else if (SPEC_DATE.equalsIgnoreCase(name)) {
		date = Long.parseLong(value);
	    } else if (SPEC_CONTACTNAME.equalsIgnoreCase(name)) {
		contactName = value;
	    } else if (SPEC_BODY.equalsIgnoreCase(name)) {
		body = value;
	    } else if (SPEC_SERVICE_CENTER.equalsIgnoreCase(name)) {
		serviceNumber = value;
	    } else if (SPEC_TYPE.equalsIgnoreCase(name)) {
		isMe = Integer.parseInt(value) >= 2;
		if (Integer.parseInt(value) == 3) {
		    isDraft = true;
		}
	    }
	}

	String normalizePhoneNumber = normalizePhoneNumber(contactNumber);
	if (StringUtils.isBlank(normalizePhoneNumber)) {
	    if (!"null".equals(serviceNumber)) {
		normalizePhoneNumber = normalizePhoneNumber(serviceNumber);
	    }
	    contactName = contactNumber;
	}

	stack.push(new Sms(date, isMe, isDraft, body, new Contact(normalizePhoneNumber, contactName)));
    }

    /**
     * Parse a "&lt;mms&gt;" element and make it a {@link Sms} model object. The Sms
     * object is then enqueued onto the <code>stack</code> parameter.
     * 
     * @param stack The stack to fill with read data.
     * @param atts  The element's attributes.
     */
    private void parseMms(Deque<Sms> stack, Attributes atts) {
	String contactName = "";
	String contactNumber = "";
	long date = 0L;
	String body = "";
	boolean isMe = false;
	boolean isDraft = false;

	for (int iAtt = 0; iAtt < atts.getLength(); iAtt++) {
	    String name = atts.getLocalName(iAtt);
	    String value = atts.getValue(iAtt);

	    if (SPEC_ADDRESS.equalsIgnoreCase(name)) {
		contactNumber = value;
	    } else if (SPEC_DATE.equalsIgnoreCase(name)) {
		date = Long.parseLong(value);
	    } else if (name.equalsIgnoreCase("snippet") && !value.equalsIgnoreCase("null")) {
		body = value;
	    } else if (SPEC_CONTACTNAME.equalsIgnoreCase(name)) {
		contactName = value;
	    } else if (SPEC_MTYPE.equalsIgnoreCase(name)) {
		isMe = Integer.parseInt(value) == 128;
	    }
	}
	stack.push(new Sms(date, isMe, isDraft, body, new Contact(normalizePhoneNumber(contactNumber), contactName)));
    }

    /**
     * Read attribute's content of a "&lt;part&gt;" element.
     * 
     * @param stack The stack to poll for finding current MMS data.
     * @param atts  The "&lt;part&gt;" element's attributes.
     */
    private void parseMmsPart(Deque<Sms> stack, Attributes atts) {

	String data = null;
	String imageName = "";
	String mime = null;
	String body = "";
	int width = 0;
	int height = 0;

	for (int iAtt = 0; iAtt < atts.getLength(); iAtt++) {
	    String name = atts.getLocalName(iAtt);
	    String value = atts.getValue(iAtt);

	    if (SPEC_TEXT.equalsIgnoreCase(name)) {
		body = value;

		// Find the image width
		Matcher matcher = Pattern.compile("<root-layout.*width=\"([0-9]*)[^\"]*\"").matcher(value);
		if (matcher.find()) {
		    width = Integer.parseInt(matcher.group(1));
		}
		// Find the image height
		matcher = Pattern.compile("<root-layout.*height=\"([0-9]*)[^\"]*\"").matcher(value);
		if (matcher.find()) {
		    height = Integer.parseInt(matcher.group(1));
		}
	    } else if (SPEC_CL.equalsIgnoreCase(name)) {
		imageName = value;
	    } else if (SPEC_CT.equalsIgnoreCase(name)) {
		mime = value;
	    } else if (SPEC_DATA.equalsIgnoreCase(name)) {
		data = value;
	    }
	}

	Sms mms = stack.peek();
	if (mime != null && mime.toLowerCase().startsWith("image/") && data != null) {
	    ImagePart image = decodeImage(mime, data, imageName, width, height);
	    mms.add(image);
	}
	if ("null".equals(body)) {
	    body = "";
	}
	mms.setBody(body);
    }

    /**
     * Read attribute's content of a "&lt;addr&gt;" element.
     * 
     * @param stack The stack to poll for finding current MMS data.
     * @param atts  The "&lt;addr&gt;" element's attributes.
     */
    private void parseMmsAddr(Deque<Sms> stack, Attributes atts) {

	Sms mms = stack.peek();
	String phoneNumber = null;
	int type = 0;

	for (int iAtt = 0; iAtt < atts.getLength(); iAtt++) {
	    String name = atts.getLocalName(iAtt);
	    String value = atts.getValue(iAtt);

	    if (SPEC_ADDRESS.equalsIgnoreCase(name)) {
		phoneNumber = normalizePhoneNumber(value);
	    } else if (SPEC_TYPE.equalsIgnoreCase(name)) {
		type = Integer.parseInt(value);
	    }
	}
	mms.addRecipient(phoneNumber, type);
    }

    /**
     * Decode bytes of an image and make it an ImagePart model object with metadata.
     * 
     * @param width     Image's width, as declared in the XML file.
     * @param height    Image's height, as declared in the XML file.
     * @param mime      MIME type of the image.
     * @param data      Bytes to decode.
     * @param imageName The name of the image.
     * @return An ImagePart containing metadata about its dimensions and a base64
     *         encoded string.
     */
    private ImagePart decodeImage(String mime, String data, String imageName, int width, int height) {
	try {
	    Image i = ImageUtil.decodeBase64Mime(data);
	    width = (int) i.getWidth();
	    height = (int) i.getHeight();
	} catch (IOException e) {
	    logger.log(Level.ERROR, "decodeImage()", e);
	}
	return new ImagePart(mime, imageName, data, width, height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
	return super.equals(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
	return super.hashCode();
    }

}
