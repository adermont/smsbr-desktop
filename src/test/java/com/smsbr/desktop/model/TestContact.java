package com.smsbr.desktop.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.MatchType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

class TestContact {

    @Test
    @DisplayName("Contact constructor preserves empty strings")
    void testGetters() {
	Contact contact = new Contact("0684552136", "Some Contact");
	assertEquals("Some Contact", contact.getCompleteName());
	assertEquals("0684552136", contact.getPhoneNumber());
    }

    @Test
    @DisplayName("Contact constructor preserves empty strings")
    void testConstructorWithEmptyStrings() {
	Contact emptyContact = new Contact("", "");
	assertEquals("", emptyContact.getCompleteName());
	assertEquals("", emptyContact.getPhoneNumber());
    }

    @Test
    @DisplayName("Contact constructor preserves null strings")
    void testConstructorWithNullStrings() {
	Contact emptyContact = new Contact(null, null);
	assertNull(emptyContact.getCompleteName());
	assertNull(emptyContact.getPhoneNumber());
    }

    @Test
    @DisplayName("Test phone number normalization by country code")
    void testFRPhoneNumber() {
	Contact c1 = new Contact("0684552136", "Random french number");
	Contact c2 = new Contact("+33684552136", "Same french number in international format");

	try {
	    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
	    PhoneNumber phone1 = phoneNumberUtil.parse(c1.getPhoneNumber(), "FR");
	    PhoneNumber phone2 = phoneNumberUtil.parse(c2.getPhoneNumber(), "FR");

	    assertTrue(phoneNumberUtil.isValidNumber(phone1));
	    assertTrue(phoneNumberUtil.isValidNumber(phone2));

	    assertSame(MatchType.EXACT_MATCH, phoneNumberUtil.isNumberMatch(phone1, phone2));

	} catch (NumberParseException e) {
	    fail(e.getLocalizedMessage());
	}
    }

    @Test
    @DisplayName("Test US phone numbers normalization")
    void testUSPhoneNumbers() {
	Contact c1 = new Contact("815-7952701", "Random US number");
	Contact c2 = new Contact("+18157952701", "Same US number in international format");

	try {
	    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
	    PhoneNumber phone1 = phoneNumberUtil.parse(c1.getPhoneNumber(), "US");
	    PhoneNumber phone2 = phoneNumberUtil.parse(c2.getPhoneNumber(), "US");

	    assertTrue(phoneNumberUtil.isValidNumber(phone1));
	    assertTrue(phoneNumberUtil.isValidNumber(phone2));

	    assertSame(MatchType.EXACT_MATCH, phoneNumberUtil.isNumberMatch(phone1, phone2));

	} catch (NumberParseException e) {
	    fail(e.getLocalizedMessage());
	}
    }

    @Test
    @DisplayName("Test FR phone numbers from US point of view")
    void testFRPhoneNumbersFromUS() {
	Contact c1 = new Contact("+33684552136", "Random FR number");
	Contact c2 = new Contact("01133684552136", "Same FR number in US format with 011 external call prefix");

	try {
	    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
	    PhoneNumber phone1 = phoneNumberUtil.parse(c1.getPhoneNumber(), "US");
	    PhoneNumber phone2 = phoneNumberUtil.parse(c2.getPhoneNumber(), "US");
	    PhoneNumber phone2bad = phoneNumberUtil.parse(c2.getPhoneNumber(), "FR");

	    assertTrue(phoneNumberUtil.isValidNumber(phone1));
	    assertTrue(phoneNumberUtil.isValidNumber(phone2));
	    assertFalse(phoneNumberUtil.isValidNumber(phone2bad));

	    assertSame(MatchType.EXACT_MATCH, phoneNumberUtil.isNumberMatch(phone1, phone2));
	    assertSame(MatchType.SHORT_NSN_MATCH, phoneNumberUtil.isNumberMatch(phone2, phone2bad));

	} catch (NumberParseException e) {
	    fail(e.getLocalizedMessage());
	}
    }
}
