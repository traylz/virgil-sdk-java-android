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
package com.virgilsecurity.sdk.highlevel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.virgilsecurity.sdk.client.VirgilClient;
import com.virgilsecurity.sdk.client.exceptions.VirgilCardIsNotFoundException;
import com.virgilsecurity.sdk.client.model.Card;
import com.virgilsecurity.sdk.client.model.CardScope;
import com.virgilsecurity.sdk.client.model.GlobalIdentityType;
import com.virgilsecurity.sdk.client.model.dto.SearchCriteria;
import com.virgilsecurity.sdk.client.requests.CreateCardRequest;
import com.virgilsecurity.sdk.client.requests.RevokeCardRequest;
import com.virgilsecurity.sdk.client.utils.ConvertionUtils;
import com.virgilsecurity.sdk.client.utils.StringUtils;
import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.PublicKey;
import com.virgilsecurity.sdk.crypto.exceptions.EmptyArgumentException;
import com.virgilsecurity.sdk.crypto.exceptions.NullArgumentException;

/**
 * A Virgil Card is the main entity of the Virgil Security services, it includes
 * an information about the user and his public key. The Virgil Card identifies
 * the user by one of his available types, such as an email, a phone number,
 * etc.
 *
 * @author Andrii Iakovenko
 *
 */
public class VirgilCard {

	private Card model;

	/**
	 * Create a new instance of {@code VirgilCard}
	 *
	 */
	VirgilCard(Card model) {
		this.model = model;
	}

	/**
	 * Encrypts the specified data for current {@linkplain VirgilCard}
	 * recipient.
	 * 
	 * @param data
	 *            the data to be encrypted.
	 * @return encrypted data.
	 */
	public byte[] encrypt(byte[] data) {
		if (data == null) {
			throw new NullArgumentException("data");
		}

		Crypto crypto = VirgilConfig.getService(Crypto.class);
		PublicKey publicKey = crypto.importPublicKey(this.getPublicKey());

		byte[] cipherdata = crypto.encrypt(data, publicKey);

		return cipherdata;
	}

	/**
	 * Verifies the specified data and signature with current
	 * {@linkplain VirgilCard} recipient.
	 * 
	 * @param data
	 *            The data to be verified.
	 * @param signature
	 *            The signature used to verify the data integrity.
	 * @return {@code true} if verification successed.
	 */
	public boolean verify(byte[] data, byte[] signature) {
		if (data == null) {
			throw new NullArgumentException("data");
		}

		if (signature == null) {
			throw new NullArgumentException("signature");
		}

		Crypto crypto = VirgilConfig.getService(Crypto.class);
		PublicKey publicKey = crypto.importPublicKey(this.getPublicKey());

		boolean isValid = crypto.verify(data, signature, publicKey);

		return isValid;
	}

	/**
	 * Gets the {@code VirgilCard} by specified identifier.
	 * 
	 * @param cardId
	 *            The identifier that represents a {@linkplain VirgilCard}.
	 * @return The Virgil Card.
	 */
	public static VirgilCard get(String cardId) {
		VirgilClient client = VirgilConfig.getService(VirgilClient.class);
		Card virgilCardDto = client.getCard(cardId);

		if (virgilCardDto == null) {
			throw new VirgilCardIsNotFoundException();
		}

		return new VirgilCard(virgilCardDto);
	}

	/**
	 * Finds the {@linkplain VirgilCard}s in global scope by specified criteria.
	 * 
	 * @param identity
	 *            The identity.
	 * @return A list of found {@linkplain VirgilCard}s.
	 */
	public static VirgilCards findGlobal(String identity) {
		return findGlobal(identity, GlobalIdentityType.EMAIL);
	}

	/**
	 * Finds the {@linkplain VirgilCard}s in global scope by specified criteria.
	 * 
	 * @param identity
	 *            The identity.
	 * @param type
	 *            Type of the identity.
	 * @return A list of found {@linkplain VirgilCard}s.
	 */
	public static VirgilCards findGlobal(String identity, GlobalIdentityType type) {
		if (identity == null) {
			throw new NullArgumentException("identity");
		}

		return findGlobal(Arrays.asList(identity), type);
	}

	/**
	 * Finds the {@linkplain VirgilCard}s in global scope by specified criteria.
	 * 
	 * @param identities
	 *            The identities.
	 * @return A list of found {@linkplain VirgilCard}s.
	 */
	public static VirgilCards findGlobal(List<String> identities) {
		return findGlobal(identities, GlobalIdentityType.EMAIL);
	}

	/**
	 * Finds the {@linkplain VirgilCard}s in global scope by specified criteria.
	 * 
	 * @param identities
	 *            The identities.
	 * @param type
	 *            Type of the identity.
	 * @return A list of found {@linkplain VirgilCard}s.
	 */
	public static VirgilCards findGlobal(List<String> identities, GlobalIdentityType type) {
		if (identities == null) {
			throw new NullArgumentException("identities");
		}

		VirgilClient client = VirgilConfig.getService(VirgilClient.class);

		SearchCriteria criteria = new SearchCriteria();
		criteria.addIdentities(identities);
		criteria.setIdentityType(type.getValue());
		criteria.setScope(CardScope.GLOBAL);

		List<Card> cardModels = client.searchCards(criteria);

		VirgilCards virgilCards = new VirgilCards();
		for (Card card : cardModels) {
			virgilCards.add(new VirgilCard(card));
		}

		return virgilCards;
	}

	/**
	 * Finds the {@linkplain VirgilCard}s by specified criteria.
	 * 
	 * @param identity
	 *            The identity.
	 * @param type
	 *            Type of the identity.
	 * @return A list of found {@linkplain VirgilCard}s.
	 */
	public static VirgilCards find(String identity, String type) {
		if (identity == null) {
			throw new NullArgumentException("identity");
		}

		return find(Arrays.asList(identity), type);
	}

	/**
	 * Finds the {@linkplain VirgilCard}s by specified criteria.
	 * 
	 * @param identities
	 *            The identities.
	 * @param type
	 *            Type of the identity.
	 * @return A list of found {@linkplain VirgilCard}s.
	 */
	public static VirgilCards find(List<String> identities, String type) {
		if (identities == null) {
			throw new NullArgumentException("identities");
		}
		for (String identity : identities) {
			if (identity == null) {
				throw new NullArgumentException("identity");
			}
		}

		VirgilClient client = VirgilConfig.getService(VirgilClient.class);

		SearchCriteria criteria = SearchCriteria.byIdentities(identities);
		criteria.setIdentityType(type);

		List<Card> cardModels = client.searchCards(criteria);

		VirgilCards virgilCards = new VirgilCards();
		for (Card card : cardModels) {
			virgilCards.add(new VirgilCard(card));
		}

		return virgilCards;
	}

	/**
	 * Creates a new {@linkplain VirgilCard} by request.
	 * 
	 * @param request
	 *            The request.
	 * @return The created card.
	 */
	public static VirgilCard create(CreateCardRequest request) {
		VirgilClient client = VirgilConfig.getService(VirgilClient.class);
		Card card = client.createCard(request);

		return new VirgilCard(card);
	}

	/**
	 * Revokes a {@linkplain VirgilCard} by revocation request.
	 * 
	 * @param request
	 *            The request.
	 */
	public static void revoke(RevokeCardRequest request) {
		VirgilClient client = VirgilConfig.getService(VirgilClient.class);
		client.revokeCard(request);
	}

	/**
	 * Encrypts the text.
	 * 
	 * @param text
	 *            The text to encrypt.
	 * @return The encrypted data.
	 * 
	 * @throws EmptyArgumentException
	 */
	public byte[] encryptText(String text) {
		if (StringUtils.isBlank(text)) {
			throw new EmptyArgumentException("text");
		}

		return encrypt(ConvertionUtils.toBytes(text));
	}

	/**
	 * Verifies that a digital signature is valid for specified text.
	 * 
	 * @param text
	 *            The text to encrypt.
	 * @param signature
	 *            The signature.
	 * @return {@code true} if the signature is valid; otherwise, {@code false}.
	 */
	public boolean verifyText(String text, byte[] signature) {
		if (StringUtils.isBlank(text)) {
			throw new EmptyArgumentException("text");
		}

		return verify(ConvertionUtils.toBytes(text), signature);
	}

	/**
	 * Gets the unique identifier for the Virgil Card.
	 * 
	 * @return the unique identifier for the Virgil Card.
	 */
	public String getId() {
		return model.getId();
	}

	/**
	 * Gets the value of current Virgil Card identity.
	 * 
	 * @return the value of current Virgil Card identity.
	 */
	public String getIdentity() {
		return model.getIdentity();
	}

	/**
	 * Gets the type of current Virgil Card identity.
	 * 
	 * @return the type of current Virgil Card identity.
	 */
	public String getIdentityType() {
		return model.getIdentityType();
	}

	/**
	 * Gets the custom parameters.
	 * 
	 * @return the custom parameters.
	 */
	public Map<String, String> getData() {
		return model.getData();
	}

	/**
	 * Gets the Public Key of current Virgil Card.
	 * 
	 * @return the Public Key of current Virgil Card.
	 */
	public byte[] getPublicKey() {
		return model.getPublicKey();
	}
}
