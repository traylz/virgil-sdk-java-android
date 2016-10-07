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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.virgilsecurity.sdk.client.exceptions.CardValidationException;
import com.virgilsecurity.sdk.client.exceptions.VirgilCardServiceException;
import com.virgilsecurity.sdk.client.exceptions.VirgilIdentityServiceException;
import com.virgilsecurity.sdk.client.exceptions.VirgilServiceException;
import com.virgilsecurity.sdk.client.model.Card;
import com.virgilsecurity.sdk.client.model.CardScope;
import com.virgilsecurity.sdk.client.model.dto.CreateCardModel;
import com.virgilsecurity.sdk.client.model.dto.ErrorResponse;
import com.virgilsecurity.sdk.client.model.dto.SearchCriteria;
import com.virgilsecurity.sdk.client.model.dto.SearchRequest;
import com.virgilsecurity.sdk.client.model.dto.SignedResponseModel;
import com.virgilsecurity.sdk.client.model.identity.Action;
import com.virgilsecurity.sdk.client.model.identity.Confirmation;
import com.virgilsecurity.sdk.client.model.identity.Identity;
import com.virgilsecurity.sdk.client.model.identity.Token;
import com.virgilsecurity.sdk.client.requests.CreateCardRequest;
import com.virgilsecurity.sdk.client.requests.RevokeCardRequest;
import com.virgilsecurity.sdk.client.utils.ConvertionUtils;
import com.virgilsecurity.sdk.client.utils.StringUtils;
import com.virgilsecurity.sdk.crypto.exceptions.EmptyArgumentException;
import com.virgilsecurity.sdk.crypto.exceptions.NullArgumentException;

/**
 * @author Andrii Iakovenko
 *
 */
public class VirgilClient {

	private VirgilClientContext context;

	private CardValidator cardValidator;

	/**
	 * Create a new instance of {@code VirgilClient}
	 *
	 * @param accessToken
	 */
	public VirgilClient(String accessToken) {
		this.context = new VirgilClientContext(accessToken);
	}

	/**
	 * Create a new instance of {@code VirgilClient}
	 *
	 * @param context
	 */
	public VirgilClient(VirgilClientContext context) {
		this.context = context;
	}

	/**
	 * Verify identity.
	 * 
	 * @param type
	 *            The type of verified identity.
	 * @param value
	 *            The value of verified identity.
	 * @return action id.
	 */
	@Deprecated
	private String verify(String type, String value) {
		String body = ConvertionUtils.getGson().toJson(new Identity(type, value));

		URIBuilder builder;
		try {
			builder = new URIBuilder(context.getIdentityServiceAddress());
			builder.setPath("/v1/verify");

			HttpPost postRequest = (HttpPost) createRequest(HttpPost.METHOD_NAME);
			postRequest.setURI(builder.build());
			postRequest.setEntity(new StringEntity(body));

			Action action = execute(postRequest, Action.class);
			return action.getActionId();

		} catch (Exception e) {
			throw new VirgilIdentityServiceException(e);
		}
	}

	/**
	 * Confirms the identity from the {@linkplain #verify(String, String)
	 * verify} step to obtain an identity confirmation token.
	 * 
	 * @param actionId
	 *            the action identifier.
	 * @param confirmationCode
	 *            the confirmation code.
	 * @param confirmationToken
	 *            the confirmation token.
	 * @return
	 * @throws ServiceException
	 */
	@Deprecated
	private Identity confirm(String actionId, String confirmationCode, Token confirmationToken) {

		Confirmation confirmation = new Confirmation();
		confirmation.setActionId(actionId);
		confirmation.setConfirmationCode(confirmationCode);
		confirmation.setToken(confirmationToken);
		String body = ConvertionUtils.getGson().toJson(confirmation);

		URIBuilder builder;
		try {
			builder = new URIBuilder(context.getIdentityServiceAddress());
			builder.setPath("/v1/confirm");

			HttpPost postRequest = (HttpPost) createRequest(HttpPost.METHOD_NAME);
			postRequest.setURI(builder.build());
			postRequest.setEntity(new StringEntity(body));

			Identity identity = execute(postRequest, Identity.class);
			return identity;

		} catch (Exception e) {
			throw new VirgilIdentityServiceException(e);
		}
	}

	/**
	 * @param request
	 * @return
	 * @throws VirgilServiceException
	 */
	public Card createCard(CreateCardRequest request) throws VirgilServiceException {
		URIBuilder builder;
		try {
			builder = new URIBuilder(context.getCardsServiceAddress());
			builder.setPath("/v4/card");

			HttpPost postRequest = (HttpPost) createRequest(HttpPost.METHOD_NAME);
			postRequest.setURI(builder.build());

			String body = request.export();
			postRequest.setEntity(new StringEntity(body));

			SignedResponseModel responseModel = execute(postRequest, SignedResponseModel.class);
			return responseToCard(responseModel);

		} catch (Exception e) {
			throw new VirgilCardServiceException(e);
		}
	}

	/**
	 * @param cardId
	 * @return
	 */
	public Card getCard(String cardId) {
		URIBuilder builder;
		try {
			builder = new URIBuilder(context.getReadOnlyCardsServiceAddress());
			builder.setPath("/v4/card/" + cardId);

			HttpGet postRequest = (HttpGet) createRequest(HttpGet.METHOD_NAME);
			postRequest.setURI(builder.build());

			SignedResponseModel responseModel = execute(postRequest, SignedResponseModel.class);
			Card card = responseToCard(responseModel);
			validateCards(Arrays.asList(card));

			return card;

		} catch (Exception e) {
			throw new VirgilCardServiceException(e);
		}
	}

	/**
	 * @param request
	 */
	public void revokeCard(RevokeCardRequest request) {
		URIBuilder builder;
		try {
			builder = new URIBuilder(context.getCardsServiceAddress());
			builder.setPath("/v4/card/" + request.getCardId());

			HttpEntityEnclosingRequestBase postRequest = (HttpEntityEnclosingRequestBase) createRequest(
					HttpDelete.METHOD_NAME);
			postRequest.setURI(builder.build());
			postRequest.setEntity(new StringEntity(request.export()));

			execute(postRequest, Void.class);

		} catch (VirgilServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new VirgilCardServiceException(e);
		}
	}

	/**
	 * @param criteria
	 * @return
	 */
	public List<Card> searchCards(SearchCriteria criteria) {
		if (criteria == null) {
			throw new NullArgumentException("criteria");
		}

		if (criteria.getIdentities().isEmpty()) {
			throw new EmptyArgumentException("criteria");
		}

		SearchRequest body = new SearchRequest();

		body.setIdentities(criteria.getIdentities());

		if (!StringUtils.isBlank(criteria.getIdentityType())) {
			body.setIdentityType(criteria.getIdentityType());
		}

		if (criteria.getScope() == CardScope.GLOBAL) {
			body.setScope(criteria.getScope());
		}

		URIBuilder builder;
		try {
			builder = new URIBuilder(context.getReadOnlyCardsServiceAddress());
			builder.setPath("/v4/card/actions/search");

			HttpPost postRequest = (HttpPost) createRequest(HttpPost.METHOD_NAME);
			postRequest.setURI(builder.build());
			postRequest.setEntity(new StringEntity(ConvertionUtils.getGson().toJson(body)));

			SignedResponseModel[] responseModels = execute(postRequest, SignedResponseModel[].class);

			List<Card> cards = new ArrayList<>();
			for (SignedResponseModel responseModel : responseModels) {
				cards.add(responseToCard(responseModel));
			}

			validateCards(cards);

			return cards;

		} catch (Exception e) {
			throw new VirgilCardServiceException(e);
		}
	}

	private Card responseToCard(SignedResponseModel responseModel) {
		CreateCardModel model = ConvertionUtils.getGson()
				.fromJson(ConvertionUtils.base64ToString(responseModel.getContentSnapshot()), CreateCardModel.class);

		Card card = new Card();
		card.setId(responseModel.getCardId());
		card.setSnapshot(ConvertionUtils.base64ToArray(responseModel.getContentSnapshot()));
		card.setIdentity(model.getIdentity());
		card.setIdentityType(model.getIdentityType());
		card.setPublicKey(ConvertionUtils.base64ToArray(model.getPublicKey()));

		if (model.getInfo() != null) {
			card.setDevice(model.getInfo().getDevice());
			card.setDeviceName(model.getInfo().getDeviceName());
		}

		if (model.getData() != null) {
			card.setData(Collections.unmodifiableMap(model.getData()));
		}
		card.setScope(model.getScope());
		card.setVersion(responseModel.getMeta().getVersion());

		Map<String, byte[]> signatures = new HashMap<>();
		if ((responseModel.getMeta() != null) && (responseModel.getMeta().getSignatures() != null)) {
			for (Entry<String, String> entry : responseModel.getMeta().getSignatures().entrySet()) {
				signatures.put(entry.getKey(), ConvertionUtils.base64ToArray(entry.getValue()));
			}
		}

		card.setSignatures(signatures);

		return card;
	}

	private CloseableHttpClient getHttpClient() {
		return HttpClients.createDefault();
	}

	private HttpRequestBase createRequest(String method) {
		HttpRequestBase requestBase = null;
		switch (method) {
		case HttpPost.METHOD_NAME:
			requestBase = new HttpPost();
			break;
		case HttpDelete.METHOD_NAME:
			requestBase = new HttpEntityEnclosingRequestBase() {

				@Override
				public String getMethod() {
					return HttpDelete.METHOD_NAME;
				}
			};
			break;
		default:
			requestBase = new HttpGet();
		}
		requestBase.addHeader("Authorization", "VIRGIL " + context.getAccessToken());
		requestBase.addHeader("Content-Type", "application/json; charset=utf-8");

		return requestBase;
	}

	private <T> T execute(HttpRequestBase requestBase, Class<T> clazz) {
		try (CloseableHttpResponse response = getHttpClient().execute(requestBase)) {
			if (response.getStatusLine().getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {

				// Get error code from request
				try (InputStream instream = response.getEntity().getContent();) {
					String body = ConvertionUtils.toString(instream);
					if (!StringUtils.isBlank(body)) {
						ErrorResponse error = ConvertionUtils.getGson().fromJson(body, ErrorResponse.class);
						throw new VirgilCardServiceException(error.getCode());
					}
				}
				throw new VirgilCardServiceException();
			} else if (clazz.isAssignableFrom(Void.class)) {
				return null;
			} else {
				HttpEntity entity = response.getEntity();
				try (InputStream instream = entity.getContent();) {
					String body = ConvertionUtils.toString(instream);
					return ConvertionUtils.getGson().fromJson(body, clazz);
				}
			}
		} catch (Exception e) {
			throw new VirgilCardServiceException(e);
		}
	}

	private void validateCards(Collection<Card> cards) {
		if (this.cardValidator == null) {
			return;
		}
		List<Card> invalidCards = new ArrayList<>();

		for (Card card : cards) {
			if (!this.cardValidator.validate(card)) {
				invalidCards.add(card);
			}
		}

		if (!invalidCards.isEmpty()) {
			throw new CardValidationException(invalidCards);
		}
	}

	/**
	 * Sets the card validator.
	 * 
	 * @param cardValidator
	 *            the cardValidator to set
	 */
	public void setCardValidator(CardValidator cardValidator) {
		if (cardValidator == null) {
			throw new NullArgumentException("cardValidator");
		}

		this.cardValidator = cardValidator;
	}

}
