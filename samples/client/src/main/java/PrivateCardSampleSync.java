
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

import java.util.List;
import java.util.UUID;

import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.privatekey.PrivateKeyInfo;
import com.virgilsecurity.sdk.client.model.publickey.PublicKeyInfo;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria.Builder;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.client.utils.ValidationTokenGenerator;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;
import com.virgilsecurity.sdk.crypto.Password;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.utils.ConversionUtils;

/**
 * This sample shows how to make synchronous calls to Identity service.
 * 
 * @author Andrii Iakovenko
 *
 */
public class PrivateCardSampleSync {

	private static final String PRIVATE_IDENTITY_TYPE = "guid";

	private static final String ACCESS_TOKEN = "{ACCESS_TOKEN}";

	// Obtain your App's private key at https://developer.virgilsecurity.com
	// Don't save App's private key at your client application!
	private static final String APP_PRIVATE_KEY = "{PRIVATE_KEY}";
	private static final String APP_PRIVATE_KEY_PASSWORD = "{PRIVATE_KEY_PASSWORD}";

	public static void main(String[] args) throws Exception {
		ClientFactory factory = new ClientFactory(ACCESS_TOKEN);

		PrivateKey appPrivateKey = new PrivateKey(APP_PRIVATE_KEY);
		Password appPrivateKeyPassword = new Password(APP_PRIVATE_KEY_PASSWORD);

		/** Cards and Public Keys */

		// Build an identity
		String identityValue = UUID.randomUUID().toString();

		System.out.println("Generated identity:");
		System.out.println("guid : " + identityValue);

		ValidatedIdentity identity = new ValidatedIdentity(PRIVATE_IDENTITY_TYPE, identityValue);

		// You should generate new validation token for each call to Virgil
		// Services.
		// The best solution is to obtain this token from your server.
		String validationToken = ValidationTokenGenerator.generate(PRIVATE_IDENTITY_TYPE, identityValue, appPrivateKey,
				appPrivateKeyPassword);
		identity.setToken(validationToken);

		// Publish a Virgil Card
		KeyPair keyPair = KeyPairGenerator.generate();

		VirgilCardTemplate.Builder vcBuilder = new VirgilCardTemplate.Builder().setIdentity(identity)
				.setPublicKey(keyPair.getPublic());
		VirgilCard cardInfo = factory.getPublicKeyClient().createCard(vcBuilder.build(), keyPair.getPrivate());
		System.out.println("Created Virgil Card with ID: " + cardInfo.getId());

		// Search for Cards
		Builder criteriaBuilder = new Builder().setType(PRIVATE_IDENTITY_TYPE).setValue(identityValue);
		List<VirgilCard> cards = factory.getPublicKeyClient().search(criteriaBuilder.build());

		System.out.println("Virgil Cards found by criteria:");
		for (VirgilCard card : cards) {
			System.out.println(card.getId());
		}

		// Get a Public Key
		PublicKeyInfo publicKey = factory.getPublicKeyClient().getKey(cardInfo.getPublicKey().getId());
		System.out.println("Public key found:");
		System.out.println(ConversionUtils.fromBase64String(publicKey.getKey()));

		// Revoke a Virgil Card
		identity.setToken(ValidationTokenGenerator.generate(PRIVATE_IDENTITY_TYPE, identityValue, appPrivateKey,
				appPrivateKeyPassword));

		factory.getPublicKeyClient().deleteCard(identity, cardInfo.getId(), keyPair.getPrivate());

		/** Private Keys */

		// Obtain public key for the Private Keys Service retrieved from the
		// Public Keys Service
		SearchCriteria criteria = new SearchCriteria();
		criteria.setType(IdentityType.APPLICATION);
		criteria.setValue("com.virgilsecurity.private-keys");

		cards = factory.getPublicKeyClient().search(criteria);
		VirgilCard serviceCard = cards.get(0);

		// Create Virgil Card because previous one was removed
		identity.setToken(ValidationTokenGenerator.generate(PRIVATE_IDENTITY_TYPE, identityValue, appPrivateKey,
				appPrivateKeyPassword));

		vcBuilder = new VirgilCardTemplate.Builder().setIdentity(identity).setPublicKey(keyPair.getPublic());
		cardInfo = factory.getPublicKeyClient().createCard(vcBuilder.build(), keyPair.getPrivate());

		// Stash a Private Key
		factory.getPrivateKeyClient(serviceCard).stash(cardInfo.getId(), keyPair.getPrivate());

		// Get a Private Key
		identity.setToken(ValidationTokenGenerator.generate(PRIVATE_IDENTITY_TYPE, identityValue, appPrivateKey,
				appPrivateKeyPassword));

		PrivateKeyInfo privateKey = factory.getPrivateKeyClient(serviceCard).get(cardInfo.getId(), identity);
		System.out.println("Private key found:");
		System.out.println(ConversionUtils.fromBase64String(privateKey.getKey()));

		// Destroy a Private Key
		factory.getPrivateKeyClient(serviceCard).destroy(cardInfo.getId(), keyPair.getPrivate());
	}

}
