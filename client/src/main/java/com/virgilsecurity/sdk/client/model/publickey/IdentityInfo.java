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

import com.google.gson.annotations.SerializedName;
import com.virgilsecurity.sdk.client.model.Identity;

/**
 * This class represents detailed information about identity.
 * 
 * @author Andrii Iakovenko
 */
public class IdentityInfo extends Identity {

	/**
	 * Identity ID.
	 */
	@SerializedName("id")
	private String id;

	/**
	 * Set to <code> true</code> if identity confirmed.
	 */
	@SerializedName("is_confirmed")
	private boolean confirmed;

	/**
	 * Identity creation date.
	 */
	@SerializedName("created_at")
	private Date createdAt;

	/**
	 * Returns the identity's identifier.
	 * 
	 * @return the identity's identifier.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the identity's identifier.
	 * 
	 * @param id
	 *            the identity's identifier.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return {@code true} if identity confirmed.
	 */
	public boolean isConfirmed() {
		return confirmed;
	}

	/**
	 * @param confirmed
	 *            the identiry confirmed flag.
	 */
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	/**
	 * Returns the identity creation date.
	 * 
	 * @return the identity creation date.
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * Sets the identity creation date.
	 * 
	 * @param createdAt
	 *            the identity creation date.
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
