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

import java.util.ArrayList;
import java.util.List;

import com.smsbr.desktop.model.Contact;

/**
 * Metadata of a {@link SmsBackupFile}.
 * 
 * @author Alexandre DERMONT
 */
public class SmsBackupFileMetadata {

    private long mSizeInBytes;
    private int mMessageCount;
    private List<Contact> mContacts;

    /**
     * Create a new SmsBackupFileMetadata with the specified length.
     * 
     * @param sizeInBytes The associated {@link SmsBackupFile}'s length, in bytes.
     */
    public SmsBackupFileMetadata(long sizeInBytes) {
	super();
	this.mSizeInBytes = sizeInBytes;
	this.mMessageCount = 0;
	this.mContacts = new ArrayList<>();
    }

    /**
     * Create a new SmsBackupFileMetadata with the specified length.
     * 
     * @param sizeInBytes  The associated {@link SmsBackupFile}'s length, in bytes.
     * @param messageCount Number of messages contained in the
     *                     {@link SmsBackupFile}.
     * @param contacts     Contacts contained in the {@link SmsBackupFile}.
     */
    public SmsBackupFileMetadata(int sizeInBytes, int messageCount, List<Contact> contacts) {
	super();
	this.mSizeInBytes = sizeInBytes;
	this.mMessageCount = messageCount;
	this.mContacts = contacts;
    }

    /**
     * @return The size of the {@link SmsBackupFile} (in bytes).
     */
    public long getSizeInBytes() {
	return mSizeInBytes;
    }

    /**
     * Set the size of the {@link SmsBackupFile} in bytes.
     * 
     * @param sizeInBytes The size of the {@link SmsBackupFile} (in bytes).
     */
    public void setSizeInBytes(long sizeInBytes) {
	this.mSizeInBytes = sizeInBytes;
    }

    /**
     * Get the number of messages in the {@link SmsBackupFile}.
     * 
     * @return number of messages.
     */
    public int getMessageCount() {
	return mMessageCount;
    }

    /**
     * Set the number of messages in the {@link SmsBackupFile}.
     * 
     * @param messageCount the messageCount to set.
     */
    public void setMessageCount(int messageCount) {
	this.mMessageCount = messageCount;
    }

    /**
     * Return all the contacts found in the in the {@link SmsBackupFile}.
     * 
     * @return All the contacts.
     */
    public List<Contact> getContacts() {
	return mContacts;
    }

    /**
     * Adds a new Contact in the metadata.
     */
    public void addContact(Contact c) {
	mContacts.add(c);
    }

    /**
     * Clear all contacts and reset the messageCount. The file length in bytes is
     * left unchanged.
     */
    public void clear() {
	this.mContacts.clear();
	this.mMessageCount = 0;
    }

}
