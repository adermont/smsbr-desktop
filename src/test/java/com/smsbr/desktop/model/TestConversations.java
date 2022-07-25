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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class for class {@link Conversations}.
 */
class TestConversations {

    @Test
    @DisplayName("")
    void testGettersAndSetters() {

	Contact john = new Contact("+33695142235", "John");
	Contact debbie = new Contact("+33632145147", "Debbie");
	Sms sms1 = new Sms(1000, true, false, "Hey John!", john);
	Sms sms2 = new Sms(20000, false, false, "Hello Alex", john);
	Sms sms3 = new Sms(21000, false, false, "What's up?", john);
	Sms sms4 = new Sms(2000, false, false, "What are you doing Alex?", debbie);
	Conversations conversations = new Conversations();
	conversations.add(sms1);
	conversations.add(sms2);
	conversations.add(sms3);
	conversations.add(sms4);

	assertEquals(4, conversations.getMessageCount());

	List<Contact> contactNamesSortedByDate = conversations.getContactNamesSortedByDate(Order.ASC);
	assertEquals(2, contactNamesSortedByDate.size());
	assertEquals(john, contactNamesSortedByDate.get(0));
	assertEquals(debbie, contactNamesSortedByDate.get(1));

	List<Contact> contactsByLexOrderDesc = conversations.getContactNamesSortedByLexicographicOrder(Order.DESC);
	assertEquals(2, contactsByLexOrderDesc.size());
	assertEquals(john, contactsByLexOrderDesc.get(0));
	assertEquals(debbie, contactsByLexOrderDesc.get(1));

	List<Contact> contactsByLexOrderAsc = conversations.getContactNamesSortedByLexicographicOrder(Order.ASC);
	assertEquals(2, contactsByLexOrderAsc.size());
	assertEquals(debbie, contactsByLexOrderAsc.get(0));
	assertEquals(john, contactsByLexOrderAsc.get(1));

	List<Sms> conversationWithDebbie = conversations.getConversation(debbie, Order.ASC);
	assertEquals(1, conversationWithDebbie.size());
	assertEquals(sms4, conversationWithDebbie.get(0));

	List<Sms> conversationWithJohn = conversations.getConversation(john, Order.ASC);
	assertEquals(3, conversationWithJohn.size());
	assertEquals(sms1, conversationWithJohn.get(0));
	assertEquals(sms2, conversationWithJohn.get(1));
	assertEquals(sms3, conversationWithJohn.get(2));
    }
}
