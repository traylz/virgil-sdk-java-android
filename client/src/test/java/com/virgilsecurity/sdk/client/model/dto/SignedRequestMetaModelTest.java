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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import com.virgilsecurity.sdk.client.utils.ConvertionUtils;

/**
 * Unit tests for {@link SignedRequestMetaModel}.
 *
 * @author Andrii Iakovenko
 *
 */
public class SignedRequestMetaModelTest {

	private static final String KEY = "theKey";
	private static final String VALUE = "theValue";

	private SignedRequestMetaModel model;

	@Before
	public void setUp() {
		model = new SignedRequestMetaModel();
		Map<String, String> signatures = new HashMap<>();
		signatures.put(KEY, VALUE);

		model.setSignatures(signatures);
	}

	@Test
	public void testJson() throws ParseException {
		String json = ConvertionUtils.getGson().toJson(model);

		JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
		assertTrue(jsonObject.containsKey("signs"));
		JSONObject jsonSigns = (JSONObject) jsonObject.get("signs");
		assertEquals(ConvertionUtils.toBase64String(VALUE), jsonSigns.get(KEY));

		SignedRequestMetaModel restoredModel = ConvertionUtils.getGson().fromJson(json, SignedRequestMetaModel.class);

		assertNotNull(restoredModel);

		Map<String, String> signatures = restoredModel.getSignatures();
		assertNotNull(signatures);
		assertFalse(signatures.isEmpty());
		assertEquals(VALUE, signatures.get(KEY));
	}

}
