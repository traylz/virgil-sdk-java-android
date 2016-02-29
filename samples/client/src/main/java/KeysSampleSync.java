
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.privatekey.PrivateKeyInfo;
import com.virgilsecurity.sdk.client.model.publickey.PublicKeyInfo;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria.Builder;
import com.virgilsecurity.sdk.client.model.publickey.SignResponse;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;

/**
 * This sample shows how to make synchronous calls to Identity service.
 * 
 * @author Andrii Iakovenko
 *
 */
public class KeysSampleSync {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String accesToken = "{ACCESS_TOKEN}";

		/** Identity Check */

		// Register an identity
		System.out.println("Enter email address: ");
		String email = br.readLine();

		ClientFactory factory = new ClientFactory(accesToken);

		// Initialize the identity verification process.
		String actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, email);

		System.out.println("Action id: " + actionId);

		// Use confirmation code sent to your email box
		System.out.println("Check your email box");
		System.out.println("Enter confirmation code: ");
		String confirmationCode = br.readLine();

		// Confirm the identity and get a temporary token
		ValidatedIdentity identity = factory.getIdentityClient().confirm(actionId, confirmationCode);

		System.out.println("Validation token: " + identity.getToken());

		// Validate identity
		boolean validated = factory.getIdentityClient().validate(identity);

		if (validated) {
			System.out.println("You identity is validated");
		} else {
			System.out.println("You identity is not validated");
		}

		/** Cards and Public Keys */

		// Publish a Virgil Card
		KeyPair keyPair = KeyPairGenerator.generate();

		VirgilCardTemplate.Builder vcBuilder = new VirgilCardTemplate.Builder().setIdentity(identity)
				.setPublicKey(keyPair.getPublic());
		VirgilCard cardInfo = factory.getPublicKeyClient().createCard(vcBuilder.build(), keyPair.getPrivate());
		System.out.println("Created Virgil Card with ID: " + cardInfo.getId());

		// Search for Cards
		Builder criteriaBuilder = new Builder().setValue(email)
				// Created card is unconfirmed, thus we should include
				// unconfirmed cards
				.setIncludeUnconfirmed(true);
		List<VirgilCard> cards = factory.getPublicKeyClient().search(criteriaBuilder.build(), keyPair.getPrivate());

		System.out.println("Virgil Cards found by criteria:");
		for (VirgilCard card : cards) {
			System.out.println(card.getId());
		}

		// Search for Application Cards
		System.out.println();
		System.out.println("Enter application ID: ");
		String appId = br.readLine();

		SearchCriteria criteria = new SearchCriteria();
		criteria.setValue(appId);

		List<VirgilCard> appCards = factory.getPublicKeyClient().searchApp(criteriaBuilder.build(),
				keyPair.getPrivate());
		System.out.println("Virgil Cards found by application:");
		for (VirgilCard card : appCards) {
			System.out.println(card.getId());
		}

		// Trust Virgil Card
		System.out.println("Enter signed card ID");
		String signedCardId = br.readLine();
		System.out.println("Enter signed card hash");
		String signedCardHash = br.readLine();

		SignResponse signData = factory.getPublicKeyClient().signCard(signedCardId, signedCardHash, cardInfo.getId(),
				keyPair.getPrivate());

		// Get a Public Key
		PublicKeyInfo publicKey = factory.getPublicKeyClient().getKey(cardInfo.getPublicKey().getId());

		// Untrust a Virgil Card
		factory.getPublicKeyClient().unsignCard(signedCardId, cardInfo.getId(), keyPair.getPrivate());

		// Revoke a Virgil Card
		factory.getPublicKeyClient().deleteCard(identity, cardInfo.getId(), keyPair.getPrivate());

		/** Private Keys */
		
		// Obtain public key for the Private Keys Service retrived from the Public Keys Service
		criteria = new SearchCriteria();
		criteria.setValue("com.virgilsecurity.private-keys");

		cards = factory.getPublicKeyClient().searchApp(criteria, keyPair.getPrivate());
		VirgilCard serviceCard = cards.get(0);
				
		// Create Virgil Card because previous one was removed
		vcBuilder = new VirgilCardTemplate.Builder().setIdentity(identity)
				.setPublicKey(keyPair.getPublic());
		cardInfo = factory.getPublicKeyClient().createCard(vcBuilder.build(), keyPair.getPrivate());
		
		// Stash a Private Key
		factory.getPrivateKeyClient(serviceCard).stash(cardInfo.getId(), keyPair.getPrivate());
		
		// Get a Private Key
		actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, email);
		System.out.println("Check your email box");
		System.out.println("Enter confirmation code: ");
		confirmationCode = br.readLine();
		// use confirmation code that has been sent to you email box.
		identity = factory.getIdentityClient().confirm(actionId, confirmationCode);
		
		PrivateKeyInfo privateKey = factory.getPrivateKeyClient(serviceCard).get(cardInfo.getId(), identity);
		
		// Destroy a Private Key
		factory.getPrivateKeyClient(serviceCard).destroy(cardInfo.getId(), keyPair.getPrivate());
	}

}
