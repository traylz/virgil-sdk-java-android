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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.virgilsecurity.sdk.client.exceptions.VirgilCardServiceException;
import com.virgilsecurity.sdk.client.model.dto.SignedRequestMetaModel;
import com.virgilsecurity.sdk.client.model.dto.SignedRequestModel;
import com.virgilsecurity.sdk.client.utils.ConvertionUtils;
import com.virgilsecurity.sdk.client.utils.StringUtils;

/**
 * Base class for requests.
 *
 * @author Andrii Iakovenko
 *
 */
public abstract class SignedRequest {

	protected Map<String, String> signatures;
	protected String snapshot;

	/**
	 * Create a new instance of {@code SignedRequest}
	 *
	 */
	public SignedRequest() {
		signatures = new HashMap<>();
	}

	/**
	 * Restores the request from snapshot.
	 * 
	 * @param snapshot
	 * @param signatures
	 */
	protected abstract void restoreRequest(String snapshot, Map<String, String> signatures);

	/**
	 * Takes the request snapshot.
	 * 
	 * @return
	 */
	protected abstract String takeSnapshot();

	/**
	 * Appends the signature of request fingerprint.
	 * 
	 * @param cardId
	 * @param signature
	 */
	public void appendSignature(String cardId, String signature) {
		if (StringUtils.isBlank(cardId)) {
			throw new IllegalArgumentException("Argument 'cardId' must not be blank");
		}
		if (signature == null) {
			throw new IllegalArgumentException("Argument 'signature' must not be null");
		}

		this.signatures.put(cardId, signature);
	}

	/**
	 * @return the signatures
	 */
	public Map<String, String> getSignatures() {
		return Collections.unmodifiableMap(signatures);
	}

	private SignedRequestModel getRequestModel() {
		SignedRequestMetaModel meta = new SignedRequestMetaModel();
		meta.setSignatures(this.signatures);

		SignedRequestModel requestModel = new SignedRequestModel();
		requestModel.setContentSnapshot(this.snapshot);
		requestModel.setMeta(meta);

		return requestModel;
	}

	public String export() {
		SignedRequestModel requestModel = this.getRequestModel();

		String json = ConvertionUtils.getGson().toJson(requestModel);
		return json;
	}

	/**
	 * @param exportedRequest
	 *            The request as Base64 encoded string.
	 * @param request
	 * @return
	 */
	public static <T extends SignedRequest> SignedRequest importRequest(String exportedRequest, Class<T> clazz) {
		String jsonModel = ConvertionUtils.base64ToString(exportedRequest);
		SignedRequestModel model = ConvertionUtils.getGson().fromJson(jsonModel, SignedRequestModel.class);

		SignedRequest request = null;
		try {
			request = clazz.newInstance();
			request.snapshot = model.getContentSnapshot();
			if ((model.getMeta() != null) && (model.getMeta().getSignatures() != null)) {
				request.signatures = model.getMeta().getSignatures();
				request.restoreRequest(model.getContentSnapshot(), model.getMeta().getSignatures());
			}

			return request;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new VirgilCardServiceException(e);
		}
	}

	/**
	 * @return the snapshot
	 */
	public String getSnapshot() {
		if (snapshot == null) {
			snapshot = takeSnapshot();
		}
		return snapshot;
	}

}
