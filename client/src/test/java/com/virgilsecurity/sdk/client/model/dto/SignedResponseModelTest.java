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
package com.virgilsecurity.sdk.client.model.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.TimeZone;

import org.json.simple.parser.ParseException;
import org.junit.Test;

import com.virgilsecurity.sdk.client.utils.ConvertionUtils;

/**
 * Unit tests for {@link SignedResponseModel}
 *
 * @author Andrii Iakovenko
 *
 */
public class SignedResponseModelTest {
	
	private static final String RESPONSE = "{"
			+ "\"id\": \"64bf69bd1b1fb314e0d78be341518cca806dc7fa5e974b39052c2990c52d60f4\","
			+ "\"content_snapshot\": \"eyJpZGVudGl0eSI6ImFsaWNlIiwiaWRlbnRpdHlfdHlwZSI6InVzZXJuYW1lIiwicHVibGljX2tleSI6Ik1Db3dCUVlESzJWd0F5RUEvdDFWNDVUelY3TVh0ZTdBeHRmUUZkRFNMTFora3dXd0I1eVhnMzUzNTBnXHUwMDNkIiwic2NvcGUiOiJhcHBsaWNhdGlvbiIsImRhdGEiOnt9fQ==\","
			+ "\"meta\": {"
			+ "  \"created_at\": \"2016-10-06T05:02:06+0000\","
			+ "  \"card_version\": \"4.0\","
			+ "  \"signs\": {"
			+ "    \"1ef2e45f6100792bc600828f1425b27ce7655a80543118f375bd894d7313aa00\": \"MFEwDQYJYIZIAWUDBAICBQAEQM4t47gqt2rs7IQYZF9fLy0E4ZTrFU3Dpm8E8Ue5kGQEjM9go7AI8xZpFoFuKTMQ6w4uPl51DlivU3\\/ZcZc1Xgc=\","
			+ "    \"64bf69bd1b1fb314e0d78be341518cca806dc7fa5e974b39052c2990c52d60f4\": \"MFEwDQYJYIZIAWUDBAICBQAEQLHSo0K7jfNRC62loDt2xwEdH1+zbvXGckiuXWHiuSLtEPDZKUYPLXsMBsqIv5k11Ih+Tn9508UaCdeJLIQOJQ4=\","
			+ "    \"e680bef87ba75d331b0a02bfa6a20f02eb5c5ba9bc96fc61ca595404b10026f4\": \"MFEwDQYJYIZIAWUDBAICBQAEQHbdNn0piTwXvmHctml73J6M5WTRc4nK8k7bT9JcBP1Y8CppGKnMWN8mqxnxpXct6CHz0KvYZNFdIwdQQS8THgw=\""
			+ "  }"
			+ "}"
			+ "}";

	@Test
	public void fromJson() throws ParseException {
		SignedResponseModel model = ConvertionUtils.getGson().fromJson(RESPONSE, SignedResponseModel.class);
		
		assertNotNull(model);
		assertEquals("64bf69bd1b1fb314e0d78be341518cca806dc7fa5e974b39052c2990c52d60f4", model.getCardId());
		assertEquals("eyJpZGVudGl0eSI6ImFsaWNlIiwiaWRlbnRpdHlfdHlwZSI6InVzZXJuYW1lIiwicHVibGljX2tleSI6Ik1Db3dCUVlESzJWd0F5RUEvdDFWNDVUelY3TVh0ZTdBeHRmUUZkRFNMTFora3dXd0I1eVhnMzUzNTBnXHUwMDNkIiwic2NvcGUiOiJhcHBsaWNhdGlvbiIsImRhdGEiOnt9fQ==", model.getContentSnapshot());
		
		SignedResponseMetaModel meta = model.getMeta();
		assertNotNull(meta);
		assertEquals("4.0", meta.getVersion());
		
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("EEST"));
		calendar.set(2016, 9, 6, 5, 2);
		calendar.set(Calendar.SECOND, 6);
		calendar.set(Calendar.MILLISECOND, 0);
		assertEquals(calendar.getTime(), meta.getCreatedAt());
	}
}
