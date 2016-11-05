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
package com.virgilsecurity.sdk.client;

import java.net.URI;

import com.virgilsecurity.sdk.client.utils.StringUtils;

/**
 * Virgil Client Context contains common configuration parameters of Virgil
 * Client.
 *
 * @author Andrii Iakovenko
 *
 */
public class VirgilClientContext {

	private String accessToken;

	private String cardsServiceAddress;

	private String readOnlyCardsServiceAddress;

	private String identityServiceAddress;

	/**
	 * Create a new instance of {@code VirgilClientContext}
	 *
	 * @param accessToken
	 *            The application access token.
	 */
	public VirgilClientContext(String accessToken) {
		this.accessToken = accessToken;

		this.cardsServiceAddress = "https://cards.virgilsecurity.com";
		this.readOnlyCardsServiceAddress = "https://cards-ro.virgilsecurity.com";
		this.identityServiceAddress = "https://identity.virgilsecurity.com";
	}

	/**
	 * Gets the access token.
	 * 
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken
	 *            the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Gets the cards service URL.
	 * 
	 * @return the cardsServiceAddress
	 */
	public String getCardsServiceAddress() {
		return cardsServiceAddress;
	}

	/**
	 * @param address
	 *            the cardsServiceAddress to set
	 */
	public void setCardsServiceAddress(String address) {
		if (StringUtils.isBlank(address) && !isValidURI(address))
			throw new IllegalArgumentException();

		this.cardsServiceAddress = address;
	}

	/**
	 * Gets the read only cards service address.
	 * 
	 * @return the readOnlyCardsServiceAddress
	 */
	public String getReadOnlyCardsServiceAddress() {
		return readOnlyCardsServiceAddress;
	}

	/**
	 * @param address
	 *            the readOnlyCardsServiceAddress to set
	 */
	public void setReadOnlyCardsServiceAddress(String address) {
		if (StringUtils.isBlank(address) && !isValidURI(address))
			throw new IllegalArgumentException();

		this.readOnlyCardsServiceAddress = address;
	}

	/**
	 * Gets the identity service address.
	 * 
	 * @return the identityServiceAddress
	 */
	public String getIdentityServiceAddress() {
		return identityServiceAddress;
	}

	/**
	 * @param address
	 *            the identityServiceAddress to set
	 */
	public void setIdentityServiceAddress(String address) {
		if (StringUtils.isBlank(address) && !isValidURI(address))
			throw new IllegalArgumentException();

		this.identityServiceAddress = address;
	}

	/**
	 * Verify is URI well-formed.
	 * 
	 * @param uri
	 *            The URI to be verified.
	 * @return {@code true} if URI is well-formed, {@code false} in other case.
	 */
	public static boolean isValidURI(String uri) {
		if (StringUtils.isBlank(uri)) {
			return false;
		}
		try {
			URI theUri = URI.create(uri);
			return theUri.isAbsolute();
		} catch (Exception e) {
			return false;
		}
	}

}
