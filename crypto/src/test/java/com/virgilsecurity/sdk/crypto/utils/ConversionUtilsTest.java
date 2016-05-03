/*
 * Copyright (c) 2016, Virgil Security, Inc.
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of virgil nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.virgilsecurity.sdk.crypto.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test cases for ConversionUtils.
 *
 * @author Andrii Iakovenko
 *
 */
public class ConversionUtilsTest {

	private static final String DECODED_STRING = "This String constant used for String to Base64 conversion tests.";
	private static final String ENCODED_STRING = "VGhpcyBTdHJpbmcgY29uc3RhbnQgdXNlZCBmb3IgU3RyaW5nIHRvIEJhc2U2NCBjb252ZXJzaW9uIHRlc3RzLg==";

	@Test
	public void toBase64String_null() {
		String base64 = ConversionUtils.toBase64String((String) null);

		assertNull(base64);
	}

	@Test
	public void toBase64String_empty() {
		String base64 = ConversionUtils.toBase64String("");

		assertEquals("", base64);
	}

	@Test
	public void toBase64String() {
		String base64 = ConversionUtils.toBase64String(DECODED_STRING);

		assertEquals(ENCODED_STRING, base64);
	}

	@Test
	public void fromBase64String_null() {
		String base64 = ConversionUtils.fromBase64String(null);

		assertNull(base64);
	}

	@Test
	public void fromBase64String_empty() {
		String base64 = ConversionUtils.fromBase64String("");

		assertEquals("", base64);
	}

	@Test
	public void fromBase64String() {
		String base64 = ConversionUtils.fromBase64String(ENCODED_STRING);

		assertEquals(DECODED_STRING, base64);
	}

}
