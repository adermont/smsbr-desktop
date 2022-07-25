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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;

/**
 * Container for all conversations.
 *
 * @author Alexandre DERMONT
 */
public class Conversations {

    /** Map of messages by contact. */
    protected Map<Contact, List<Sms>> mMessages;

    /** Total number of messages. */
    protected int mMessageCount;

    /** Listeners. */
    protected CopyOnWriteArrayList<IConversationsListener> mListeners;

    /**
     * Builds an empty Conversations object.
     */
    public Conversations() {
	super();
	mMessages = new HashMap<>();
	mListeners = new CopyOnWriteArrayList<>();
    }

    /**
     * Add a listener to this model.
     * 
     * @param listener The listener to add.
     */
    public void addListener(IConversationsListener listener) {
	if (!mListeners.contains(listener)) {
	    mListeners.add(listener);
	}
    }

    /**
     * Remove a listener from this model.
     * 
     * @param listener The listener to remove.
     */
    public void removeListener(IConversationsListener listener) {
	mListeners.remove(listener);
    }

    protected void fireSmsAdded(Sms sms) {
	mListeners.stream().forEach(listener -> listener.onSmsAdded(sms));
    }

    protected void fireContactRemoved(Contact contact) {
	mListeners.stream().forEach(listener -> listener.onContactRemoved(contact));
    }

    /**
     * Adds a new message.
     *
     * @param message The message to add.
     */
    public void add(Sms message) {
	List<Sms> list = mMessages.get(message.getContact());
	if (list == null) {
	    list = new ArrayList<>();
	    mMessages.put(message.getContact(), list);
	}
	list.add(message);
	mMessageCount++;

	Collections.sort(list, (Sms o1, Sms o2) -> (int) Math.signum((double) o1.getDate() - o2.getDate()));

	fireSmsAdded(message);
    }

    /**
     * Remove all SMS of contacts in the list.
     * 
     * @param contacts Contacts whose SMS should be removed.
     */
    public void removeAllConversations(List<Contact> contacts) {
	contacts.forEach(c -> {
	    mMessages.remove(c);
	    fireContactRemoved(c);
	});
    }

    /**
     * Get contact names ordered by date in the specified order (ASC or DESC).
     * 
     * @param order The desired ordering ASC or DESC.
     * @return All contacts ordered by date.
     */
    public List<Contact> getContactNamesSortedByDate(Order order) {
	List<Contact> contacts = new ArrayList<>(mMessages.keySet());
	Collections.sort(contacts, (Contact o1, Contact o2) -> {
	    List<Sms> sms1 = mMessages.get(o1);
	    List<Sms> sms2 = mMessages.get(o2);
	    return (int) Math.signum((double) sms1.get(sms1.size() - 1).getDate() - sms2.get(sms2.size() - 1).getDate())
		    * (order == Order.ASC ? -1 : 1);
	});
	return contacts;
    }

    /**
     * Get contact names ordered lexicographically in the specified order (ASC or
     * DESC).
     * 
     * @param order Sort order.
     * @return All contacts ordered by contact name.
     */
    public List<Contact> getContactNamesSortedByLexicographicOrder(Order order) {
	List<Contact> contacts = new ArrayList<>(mMessages.keySet());
	Collections.sort(contacts,
		(Contact o1, Contact o2) -> order == Order.ASC
			? StringUtils.compareIgnoreCase(o1.toString(), o2.toString())
			: StringUtils.compareIgnoreCase(o2.toString(), o1.toString()));
	return contacts;
    }

    /**
     * Get a single conversation.
     *
     * @param contact The source or destination of the conversation.
     * @param order   Sort order.
     * @return Messages of the conversation as an ordered list.
     */
    public List<Sms> getConversation(Contact contact, Order order) {
	List<Sms> messages = new ArrayList<>();
	messages.addAll(mMessages.get(contact));
	Collections.sort(messages, (Sms o1, Sms o2) -> (int) Math.signum((double) o1.getDate() - o2.getDate())
		* (order == Order.ASC ? 1 : -1));
	return messages;
    }

    /**
     * @return All messages with one or more images.
     */
    public List<Sms> getAllMessagesWithImages() {
	return mMessages.values().parallelStream()
		.flatMap(k -> k.parallelStream().filter(sms -> !sms.getImages().isEmpty())).toList();
    }

    /**
     * @param contact Contact for which we are looking for messages having images.
     * @return All messages from or to this contact with one or more images.
     */
    public List<Sms> getAllMessagesWithImages(Contact contact) {
	return mMessages.values().parallelStream().flatMap(
		k -> k.parallelStream().filter(sms -> contact.equals(sms.getContact()) && !sms.getImages().isEmpty()))
		.toList();
    }

    /**
     * Get the total message count.
     * 
     * @return the total message count.
     */
    public int getMessageCount() {
	return mMessageCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	final String LS = System.lineSeparator();

	for (Map.Entry<Contact, List<Sms>> entry : mMessages.entrySet()) {
	    sb.append(entry.getKey()).append(" :").append(LS).append("-------------------").append(LS);
	    for (Sms sms : entry.getValue()) {
		sb.append(sms).append(LS);
	    }
	}

	return sb.toString();
    }

}
