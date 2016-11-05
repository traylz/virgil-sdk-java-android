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

import java.util.Map;

import com.virgilsecurity.sdk.client.model.RevocationReason;
import com.virgilsecurity.sdk.client.model.dto.RevokeCardModel;
import com.virgilsecurity.sdk.client.utils.ConvertionUtils;

/**
 * Request used for Virgil Card revocation.
 *
 * @author Andrii Iakovenko
 *
 */
public class RevokeCardRequest extends SignedRequest {

	/**
	 * The card identifier.
	 */
	private String cardId;

	/**
	 * The revocation reason.
	 */
	private RevocationReason reason;

	/**
	 * Create a new instance of {@code RevokeCardRequest}
	 *
	 * @param cardId
	 *            the card identifier.
	 * @param reason
	 *            the reason of revocation.
	 */
	public RevokeCardRequest(String cardId, RevocationReason reason) {
		this.cardId = cardId;
		this.reason = reason;
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
		this.signatures = signatures;

		RevokeCardModel details = ConvertionUtils.getGson().fromJson(this.snapshot, RevokeCardModel.class);

		this.cardId = details.getCardId();
		this.reason = details.getReason();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.virgilsecurity.sdk.client.requests.SignedRequest#takeSnapshot()
	 */
	@Override
	protected String takeSnapshot() {
		RevokeCardModel model = new RevokeCardModel();
		model.setCardId(this.cardId);
		model.setReason(this.reason);

		String json = ConvertionUtils.getGson().toJson(model);

		return ConvertionUtils.toBase64String(json);
	}

	/**
	 * @return the cardId
	 */
	public String getCardId() {
		return cardId;
	}

	/**
	 * @return the reason
	 */
	public RevocationReason getReason() {
		return reason;
	}

}
