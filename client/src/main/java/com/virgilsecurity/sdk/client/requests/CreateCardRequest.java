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
package com.virgilsecurity.sdk.client.requests;

import java.util.HashMap;
import java.util.Map;

import com.virgilsecurity.sdk.client.model.CardScope;
import com.virgilsecurity.sdk.client.model.dto.CreateCardModel;
import com.virgilsecurity.sdk.client.model.dto.DeviceInfo;
import com.virgilsecurity.sdk.client.utils.ConvertionUtils;

/**
 * TODO: add type description
 *
 * @author Andrii Iakovenko
 *
 */
public class CreateCardRequest extends SignedRequest {

	private String identity;
	private String identityType;
	private String publicKey;
	private DeviceInfo info;
	private Map<String, String> data;

	/**
	 * Create a new instance of {@code CreateCardRequest}
	 *
	 * @param identity
	 *            The identity.
	 * @param identityType
	 *            The identity type.
	 * @param publicKey
	 *            The public key DER.
	 */
	public CreateCardRequest(String identity, String identityType, byte[] publicKey) {
		this(identity, identityType, publicKey, null, null);
	}

	/**
	 * Create a new instance of {@code CreateCardRequest}
	 *
	 * @param identity
	 *            The identity.
	 * @param identityType
	 *            The identity type.
	 * @param publicKey
	 *            The public key DER.
	 * @param data
	 *            The card meta.
	 */
	public CreateCardRequest(String identity, String identityType, byte[] publicKey, Map<String, String> data) {
		this(identity, identityType, publicKey, data, null);
	}

	/**
	 * Create a new instance of {@code CreateCardRequest}
	 *
	 * @param identity
	 *            The identity.
	 * @param identityType
	 *            The identity type.
	 * @param publicKey
	 *            The public key DER.
	 * @param data
	 *            The card meta.
	 * @param info
	 *            The device info.
	 */
	public CreateCardRequest(String identity, String identityType, byte[] publicKey, Map<String, String> data,
			DeviceInfo info) {
		this.identity = identity;
		this.identityType = identityType;
		this.publicKey = ConvertionUtils.toBase64String(publicKey);

		this.data = new HashMap<>();
		if (data != null) {
			this.data.putAll(data);
		}
		this.info = info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.virgilsecurity.sdk.client.requests.SignedRequest#restoreRequest(byte[
	 * ], java.util.Map)
	 */
	@Override
	protected void restoreRequest(String snapshot, Map<String, String> signatures) {
		this.snapshot = snapshot;

		this.signatures = new HashMap<>();
		if (signatures != null) {
			this.signatures.putAll(signatures);
		}

		CreateCardModel details = ConvertionUtils.getGson().fromJson(this.snapshot, CreateCardModel.class);

		this.identity = details.getIdentity();
		this.identityType = details.getIdentityType();
		this.publicKey = details.getPublicKey();

		this.data = new HashMap<>();
		if (details.getData() != null) {
			this.data.putAll(details.getData());
		}
		this.info = details.getInfo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.virgilsecurity.sdk.client.requests.SignedRequest#takeSnapshot()
	 */
	@Override
	protected String takeSnapshot() {
		CreateCardModel model = new CreateCardModel();
		model.setIdentity(this.identity);
		model.setIdentityType(this.identityType);
		model.setPublicKey(this.publicKey);
		model.setData(this.data);
		model.setScope(CardScope.APPLICATION);
		model.setInfo(this.info);

		String json = ConvertionUtils.getGson().toJson(model);
		return ConvertionUtils.toBase64String(json);
	}

	/**
	 * @return the identity
	 */
	public String getIdentity() {
		return identity;
	}

	/**
	 * @return the identityType
	 */
	public String getIdentityType() {
		return identityType;
	}

	/**
	 * @return the publicKey
	 */
	public String getPublicKey() {
		return publicKey;
	}

	/**
	 * @return the info
	 */
	public DeviceInfo getInfo() {
		return info;
	}

	/**
	 * @return the data
	 */
	public Map<String, String> getData() {
		return data;
	}

}
