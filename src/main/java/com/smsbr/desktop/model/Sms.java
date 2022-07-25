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
package com.smsbr.desktop.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A message with a date, a contact, a body and optional attachments (only
 * images are supported right now).
 *
 * @author Alexandre DERMONT
 */
public class Sms {

    /** The message's date (time stamp). */
    protected long mDate;
    /** Flag that indicates the phone's owner was the sender of this message. */
    protected boolean mIsMe;
    /** Flag that indicates that this message is a draft. */
    protected boolean mIsDraft;
    /** The message content. */
    protected String mBody;
    /** The contact(s) the message is incoming from/outgoing to. */
    protected Contact mContact;
    /** List of attachments. */
    protected List<ImagePart> mImages;
    /** Recipients. */
    protected List<String> mRecipients;

    /**
     * Builds a new Sms with empty images list.
     * 
     * @param date    The date the SMS was sent.
     * @param isMe    Flag for messages sent by 'me'.
     * @param isDraft Set to 'true' if the message is a draft message.
     * @param body    The message's body.
     * @param contact Contact of the conversation (ie. source or destination).
     */
    public Sms(long date, boolean isMe, boolean isDraft, String body, Contact contact) {
	super();
	this.mDate = date;
	this.mIsMe = isMe;
	this.mIsDraft = isDraft;
	this.mBody = body;
	this.mContact = contact;
	this.mImages = new ArrayList<>();
	this.mRecipients = new ArrayList<>();
    }

    /**
     * Adds an image.
     * 
     * @param image The image to add to this short message.
     */
    public void add(ImagePart image) {
	if (image != null) {
	    mImages.add(image);
	}
    }

    /**
     * Get the message's date.
     * 
     * @return the message's date.
     */
    public long getDate() {
	return mDate;
    }

    /**
     * Return the flag that indicates if I was the sender of the message.
     * 
     * @return the flag that indicates if I was the sender of the message.
     */
    public boolean isMe() {
	return mIsMe;
    }

    /**
     * Get the flag that indicates whether this message is in 'draft' state.
     * 
     * @return the flag that indicates whether this message is in 'draft' state.
     */
    public boolean isDraft() {
	return mIsDraft;
    }

    /**
     * Set the 'draft' state.
     * 
     * @param isDraft <code>true</code> if the message is in draft state,
     *                <code>false</code> else.
     */
    public void setDraft(boolean isDraft) {
	mIsDraft = isDraft;
    }

    /**
     * @return The message's content.
     */
    public String getBody() {
	return mBody;
    }

    /**
     * Set the message's content.
     * 
     * @param body The message's content.
     */
    public void setBody(String body) {
	mBody = body;
    }

    /**
     * Get the message's contact.
     * 
     * <strong>Note</strong>: for MMS with multiple recipients, the phone number of
     * the contact returned by this method may be a space-separated list of numbers.
     * 
     * @return the message's contact.
     */
    public Contact getContact() {
	return mContact;
    }

    /**
     * Get images attached to this message.
     * 
     * @return a list of {@link ImagePart}.
     */
    public List<ImagePart> getImages() {
	return mImages;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append(mIsMe ? "Me" : mContact).append(" : \n");
	sb.append(mBody).append(" - ");
	sb.append(mDate);
	return sb.toString();
    }

    /**
     * @param phoneNumber
     * @param type
     */
    public void addRecipient(String phoneNumber, int type) {
	if (type != 137) {
	    mRecipients.add(phoneNumber);
	}
    }
}
