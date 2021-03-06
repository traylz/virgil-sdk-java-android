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
package com.virgilsecurity.sdk.client.model.identity;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

/**
 * This class represents Virgil identity.
 *
 * @author Andrii Iakovenko
 *
 */
public class Identity {

	@SerializedName("type")
	private String type;

	@SerializedName("value")
	private String value;

	@SerializedName("validation_token")
	private String token;

	@SerializedName("extra_fields")
	private Map<String, String> fields;

	/**
	 * Create a new instance of {@code VerifyRequest}
	 *
	 */
	public Identity() {
	}

	/**
	 * Create a new instance of {@code VerifyRequest}
	 *
	 * @param type
	 *            the identity type.
	 * @param value
	 *            the identity value.
	 */
	public Identity(String type, String value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * Returns the identity type.
	 * 
	 * @return the identity type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the identity type.
	 * 
	 * @param type
	 *            the identity type.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the identity value.
	 * 
	 * @return the identity value.
	 * 
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the identity value.
	 * 
	 * @param value
	 *            the identity value.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the fields
	 */
	public Map<String, String> getFields() {
		return fields;
	}

	/**
	 * @param fields
	 *            the fields to set
	 */
	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

	/**
	 * Returns the validation token. Token is {@code null} for non-validated
	 * identities.
	 * 
	 * @return the validation token.
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Sets the validation token.
	 * 
	 * @param token
	 *            the validation token.
	 */
	public void setToken(String token) {
		this.token = token;
	}

}
