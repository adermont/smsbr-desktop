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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test of the class {@link Sms}.
 */
class TestSms {

    @Test
    @DisplayName("Test getters and setters of class 'Sms'")
    void testGettersAndSetters() {
	long date = System.currentTimeMillis();
	Contact c = new Contact("+33625147896", "Me");
	Sms sms = new Sms(date, true, true, "This is a draft message", c);

	assertEquals(date, sms.getDate());
	assertTrue(sms.isDraft());
	assertTrue(sms.isMe());
	assertEquals("This is a draft message", sms.getBody());

	sms.setDraft(false);
	sms.setBody("");
	assertEquals(date, sms.getDate());
	assertFalse(sms.isDraft());
	assertTrue(sms.isMe());
	assertEquals("", sms.getBody());
    }
}
