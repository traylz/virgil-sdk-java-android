/*
 * Copyright (c) 2015, Virgil Security, Inc.
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
package com.virgilsecurity.sdk.crypto;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

/**
 *
 * @author Andrii Iakovenko
 */
public class PasswordTest {

	@Test(expected = IllegalArgumentException.class)
	public void create_nullBytesPassword_throws_exception() {
		new Password((byte[]) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_nullStringPassword_throws_exception() {
		new Password((String) null);
	}

	@Test
	public void create_emptyPassword_returns_password() {
		Password password = new Password(new byte[0]);
		assertNotNull(password.getEncoded());
		assertEquals(0, password.getEncoded().length);
	}

	@Test
	public void create_PasswordByBytes() {
		Password password = new Password(new byte[20]);
		assertNotNull(password.getEncoded());
		assertEquals(20, password.getEncoded().length);
	}

	@Test
	public void create_PasswordByString() {
		Password password = new Password("ThePassword");
		assertNotNull(password.getEncoded());
		assertArrayEquals("ThePassword".getBytes(), password.getEncoded());
	}

	@Test
	public void create_Password_OfAnyLength() {
		for (int i = 1; i <= 255; i++) {
			Password password = new Password(new byte[i]);
			assertNotNull(password.getEncoded());
			assertEquals(i, password.getEncoded().length);
		}
	}

	@Test
	public void create_PasswordFromString_OfAnyLength() {
		for (int i = 1; i <= 255; i++) {
			Password password = new Password(RandomStringUtils.random(i));
			assertNotNull(password.getEncoded());
			assertTrue(password.getEncoded().length > 0);
		}
	}
}
