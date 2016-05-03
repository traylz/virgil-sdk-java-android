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

import com.google.gson.annotations.SerializedName;
import com.virgilsecurity.sdk.client.model.Identity;

/**
 * This class represents validated identity.
 * 
 * @author Andrii Iakovenko
 */
public class ValidatedIdentity extends Identity {
	
	/**
	 * Create a new instance of {@code ValidatedIdentity}
	 *
	 */
	public ValidatedIdentity() {
	}
	
	/**
	 * Create a new instance of {@code ValidatedIdentity}
	 * 
	 * @param type
	 *            the identity type
	 * @param value
	 *            the identity value
	 *
	 */
	public ValidatedIdentity(String type, String value) {
		setType(type);
		setValue(value);
	}

	@SerializedName("validation_token")
	private String token;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ValidatedIdentity {");
		sb.append("type = ").append(getType());
		sb.append(" ,").append("value = ").append(getValue());
		sb.append(" ,").append("token = ").append(token);
		return sb.append("}").toString();
	}

}
