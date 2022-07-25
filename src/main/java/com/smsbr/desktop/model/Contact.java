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
package com.smsbr.desktop.model;

import java.util.Objects;

/**
 * Represents a contact with telephone number and full contact name.
 * 
 * @author Alexandre DERMONT
 */
public class Contact {

    public static final String UNKNOWN_CONTACT = "(Unknown)";

    /** The contact's phone number. */
    private String mPhoneNumber;

    /**
     * The contact's complete name as it was declared in the user's address book.
     */
    private String mCompleteName;

    /**
     * Constructor.
     * 
     * @param phoneNumber Contact's number.
     * @param name        Contact's full name.
     */
    public Contact(String phoneNumber, String name) {
	this.mPhoneNumber = phoneNumber;
	this.mCompleteName = name;
    }

    /**
     * Get the phone number.
     * 
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
	return mPhoneNumber;
    }

    /**
     * Set the phone number.
     * 
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
	this.mPhoneNumber = phoneNumber;
    }

    /**
     * Get the contact's complete name.
     * 
     * @return the contact's complete name.
     */
    public String getCompleteName() {
	return mCompleteName;
    }

    /**
     * Set the contact's name.
     * 
     * @param completeName the complete name to set.
     */
    public void setCompleteName(String completeName) {
	this.mCompleteName = completeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
	return this.mPhoneNumber != null ? mPhoneNumber.hashCode() : System.identityHashCode(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
	if (obj instanceof Contact contact) {
	    return Objects.equals(contact.mPhoneNumber, this.mPhoneNumber);
	}
	return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	if (UNKNOWN_CONTACT.equals(mCompleteName)) {
	    return mPhoneNumber;
	}
	return mCompleteName + " (" + mPhoneNumber + ")";
    }
}
