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
package com.virgilsecurity.sdk.client.model.publickey;

import java.util.Date;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

/**
 * This class represents Virgil Card.
 * 
 * @author Andrii Iakovenko
 */
public class VirgilCard {

	/**
	 * VirgilCard ID.
	 */
	@SerializedName("id")
	private String id;

	/**
	 * Creation date.
	 */
	@SerializedName("created_at")
	private Date createdAt;

	/** Set to <code>null</code> if VirgilCard is not autorized. */
	@SerializedName("authorized_by")
	private String authorizedBy;

	/**
	 * VirgilCard hash.
	 */
	@SerializedName("hash")
	private String hash;

	/**
	 * Public Key info.
	 */
	@SerializedName("public_key")
	private PublicKeyInfo publicKey;

	/**
	 * Identity associated with VirgilCard.
	 */
	@SerializedName("identity")
	private IdentityInfo identity;

	/** Data linked to Virgil Card. */
	@SerializedName("data")
	private Map<String, String> data;

	/**
	 * Returns the Virgil Card's identifier.
	 * 
	 * @return the Virgil Card's identifier.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the Virgil Card's identifier.
	 * 
	 * @param id
	 *            the Virgil Card's identifier.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the Virgil Card's creation date.
	 * 
	 * @return the Virgil Card's creation date.
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * Sets the Virgil Card's creation date.
	 * 
	 * @param createdAt
	 *            the Virgil Card's creation date.
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * Return the name of App this card autorized by.
	 * 
	 * @return the authorizedBy flag.
	 */
	public String getAuthorizedBy() {
		return authorizedBy;
	}

	/**
	 * Set the name of App this card autorized by.
	 * 
	 * @param authorizedBy
	 *            the authorizedBy flag.
	 */
	public void setAuthorizedBy(String authorizedBy) {
		this.authorizedBy = authorizedBy;
	}

	/**
	 * Returns Virgil Card's hash.
	 * 
	 * @return the Virgil Card's hash.
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * Sets Virgil Card's hash.
	 * 
	 * @param hash
	 *            the Virgil Card's hash.
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * @return the public key
	 */
	public PublicKeyInfo getPublicKey() {
		return publicKey;
	}

	/**
	 * @param publicKey
	 *            the public key to set
	 */
	public void setPublicKey(PublicKeyInfo publicKey) {
		this.publicKey = publicKey;
	}

	/**
	 * @return the Virgil Card's identity
	 */
	public IdentityInfo getIdentity() {
		return identity;
	}

	/**
	 * @param identity
	 *            the the Virgil Card's identity
	 */
	public void setIdentity(IdentityInfo identity) {
		this.identity = identity;
	}

	/**
	 * @return the data
	 */
	public Map<String, String> getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Map<String, String> data) {
		this.data = data;
	}

}
