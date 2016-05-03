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
package com.virgilsecurity.sdk.client.service;

import java.util.List;

import com.virgilsecurity.sdk.client.model.publickey.DeleteRequest;
import com.virgilsecurity.sdk.client.model.publickey.Identities;
import com.virgilsecurity.sdk.client.model.publickey.PublicKeyInfo;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria;
import com.virgilsecurity.sdk.client.model.publickey.Sign;
import com.virgilsecurity.sdk.client.model.publickey.SignResponse;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.client.utils.Constants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * This interface represents Virgil Public Key's HTTP API.
 * 
 * @author Andrii Iakovenko
 */
public interface PublicKeyService {

	/**
	 * Create new Virgil Card.
	 * 
	 * @param template
	 *            the template of created Virgil Card.
	 * @return the created Virgil Card.
	 * 
	 * @see VirgilCard
	 * @see VirgilCardTemplate
	 * @see retrofit2.Call
	 */
	@POST("/v3/virgil-card")
	Call<VirgilCard> createCard(@Body VirgilCardTemplate template);

	/**
	 * Delete Virgil Card.
	 * 
	 * @param id
	 *            the Virgil Card's identifier.
	 * @param requestSingVCID
	 *            the {@code X-VIRGIL-REQUEST-SIGN-VIRGIL-CARD-ID} header's
	 *            value which is the same as Virgil Card's identifier.
	 * @param deleteRequest
	 *            the delete request data.
	 * @return
	 * 
	 * @see DeleteRequest
	 * @see retrofit2.Call
	 */
	@HTTP(method = "DELETE", path = "/v3/virgil-card/{virgil-card-id}", hasBody = true)
	Call<Void> delete(@Path("virgil-card-id") String id,
			@Header(Constants.Header.X_VIRGIL_REQUEST_SIGN_VIRGIL_CARD_ID) String requestSingVCID,
			@Body DeleteRequest deleteRequest);

	/**
	 * Revoke a Public Key endpoint. To revoke the Public Key it's mandatory to
	 * pass validation tokens obtained on Virgil Identity service for all
	 * confirmed Virgil Cards for this Public Key.
	 * 
	 * @param id
	 *            the Public Key's ID.
	 * @param identities
	 *            the removed identities.
	 * @return
	 * 
	 * @see retrofit2.Call
	 * @see Identities
	 */
	@DELETE("/v3/public-key/{public-key-id}")
	// TODO
	Call<Void> deleteKey(@Path("public-key-id") String id, Identities identities);

	/**
	 * Returns the information about the Virgil Card by the ID.
	 * 
	 * @param id
	 *            the Virgil Card's identifier.
	 * @return
	 * 
	 * @see retrofit2.Call
	 */
	@GET("/v3/virgil-card/{virgil-card-id}")
	Call<VirgilCard> getCard(@Path("virgil-card-id") String id);

	/**
	 * Returns the information about the Public Key by the ID. For unsigned
	 * requests the response will contain only basic information without a list
	 * of associated Virgil Cards. In case the request is signed the associated
	 * Virgil Cards collection will be returned.
	 *
	 * @param id
	 *            the PublicKey's identifier.
	 * @return
	 * 
	 * @see retrofit2.Call
	 * @see PublicKeyInfo
	 */
	@GET("/v3/public-key/{public-key-id}")
	Call<PublicKeyInfo> getKey(@Path("public-key-id") String id);

	/**
	 * Performs the search of a private application's Virgil Cards by search criteria:
	 * 
	 * @param searchCriteria
	 *            the criteria which used for Virgil Cards filtering during
	 *            search.
	 * @return
	 * 
	 * @see retrofit2.Call
	 * @see SearchCriteria
	 * @see VirgilCard
	 */
	@POST("/v3/virgil-card/actions/search")
	Call<List<VirgilCard>> search(@Body SearchCriteria searchCriteria);

	/**
	 * Performs the global search for the applications' Virgil Cards.
	 * 
	 * @param searchCriteria
	 *            the criteria which used for Virgil Cards filtering during
	 *            search.
	 * @return
	 * 
	 * @see retrofit2.Call
	 * @see SearchCriteria
	 * @see VirgilCard
	 */
	@POST("/v3/virgil-card/actions/search/app")
	Call<List<VirgilCard>> searchApp(@Body SearchCriteria searchCriteria);
	
	/**
	 * Performs the global search for the emails' Virgil Cards.
	 * 
	 * @param searchCriteria
	 *            the criteria which used for Virgil Cards filtering during
	 *            search.
	 * @return
	 * 
	 * @see retrofit2.Call
	 * @see SearchCriteria
	 * @see VirgilCard
	 */
	@POST("/v3/virgil-card/actions/search/email")
	Call<List<VirgilCard>> searchEmail(@Body SearchCriteria searchCriteria);

	/**
	 * Signs another Virgil Card addressed in the request to share the
	 * information for the signed Virgil Card.
	 * 
	 * @param signerCardId
	 *            the identifier of Virgil Card used for signing.
	 * @param requestSingVCID
	 *            the {@code X-VIRGIL-REQUEST-SIGN-VIRGIL-CARD-ID} header's
	 *            value which is the same as Virgil Card's identifier.
	 * @param sign
	 *            the sign.
	 * @return
	 * 
	 * @see retrofit2.Call
	 * @see Sign
	 * @see SignResponse
	 */
	@POST("/v3/virgil-card/{virgil-card-id}/actions/sign")
	Call<SignResponse> signCard(@Path("virgil-card-id") String signerCardId,
			@Header(Constants.Header.X_VIRGIL_REQUEST_SIGN_VIRGIL_CARD_ID) String requestSingVCID, @Body Sign sign);

	/**
	 * Removes the Sign of another Virgil Card.
	 * 
	 * @param signerCardId
	 *            the identifier of Virgil Card used for signing.
	 * @param requestSingVCID
	 *            the {@code X-VIRGIL-REQUEST-SIGN-VIRGIL-CARD-ID} header's
	 *            value which is the same as Virgil Card's identifier.
	 * @param sign
	 *            the sign.
	 * @return
	 * 
	 * @see retrofit2.Call
	 * @see Sign
	 */
	@POST("/v3/virgil-card/{virgil-card-id}/actions/unsign")
	Call<Void> unsignCard(@Path("virgil-card-id") String signerCardId,
			@Header(Constants.Header.X_VIRGIL_REQUEST_SIGN_VIRGIL_CARD_ID) String requestSingVCID, @Body Sign sign);

}
