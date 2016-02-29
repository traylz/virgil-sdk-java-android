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

import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;

/**
 * Virgil Clients factory.
 *
 * @author Andrii Iakovenko
 *
 */
public class ClientFactory {

	private String accessToken;
	private String identityBaseUrl;
	private String publicKeyBaseUrl;
	private String privateKeyBaseUrl;

	private IdentityClient identityClient;
	private PublicKeyClient publicKeyClient;
	private PrivateKeyClient privateKeyClient;

	/**
	 * Create a new instance
	 *
	 */
	public ClientFactory() {
	}

	/**
	 * Create a new instance
	 *
	 */
	public ClientFactory(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return the identityClient
	 */
	public IdentityClient getIdentityClient() {
		if (identityClient == null) {
			if (identityBaseUrl == null) {
				identityClient = new IdentityClient();
			} else {
				identityClient = new IdentityClient(identityBaseUrl);
			}
		}
		return identityClient;
	}

	/**
	 * @return the public key client.
	 */
	public PublicKeyClient getPublicKeyClient() {
		if (publicKeyClient == null) {
			if (publicKeyBaseUrl == null) {
				publicKeyClient = new PublicKeyClient(accessToken);
			} else {
				publicKeyClient = new PublicKeyClient(publicKeyBaseUrl, accessToken);
			}
		}
		return publicKeyClient;
	}

	/**
	 * @param serviceCard
	 * @return the private key client.
	 */
	public PrivateKeyClient getPrivateKeyClient(VirgilCard serviceCard) {
		if (privateKeyClient == null) {
			if (privateKeyBaseUrl == null) {
				privateKeyClient = new PrivateKeyClient(accessToken, serviceCard);
			} else {
				privateKeyClient = new PrivateKeyClient(privateKeyBaseUrl, accessToken, serviceCard);
			}
		}
		return privateKeyClient;
	}

	/**
	 * @param identityBaseUrl
	 *            the identityBaseUrl to set
	 */
	public void setIdentityBaseUrl(String identityBaseUrl) {
		this.identityBaseUrl = identityBaseUrl;
	}

	/**
	 * @param publicKeyBaseUrl
	 *            the publicKeyBaseUrl to set
	 */
	public void setPublicKeyBaseUrl(String publicKeyBaseUrl) {
		this.publicKeyBaseUrl = publicKeyBaseUrl;
	}

	/**
	 * @param privateKeyBaseUrl
	 *            the privateKeyBaseUrl to set
	 */
	public void setPrivateKeyBaseUrl(String privateKeyBaseUrl) {
		this.privateKeyBaseUrl = privateKeyBaseUrl;
	}

}
