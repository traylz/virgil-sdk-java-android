
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.model.Identity;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria.Builder;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.crypto.CryptoHelper;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;

/**
 * This sample will help you get started using the Crypto Library and Virgil
 * Keys Services for the most popular platforms and languages.
 * 
 * @author Andrii Iakovenko
 *
 */
public class Quickstart {

	public static void main(String[] args) throws Exception {
		String accesToken = "{ACCESS_TOKEN}";
		String senderEmail = "sender-test@virgilsecurity.com";

		// Step 0. Initialization

		/**
		 * Initialize the client factory instance using access token.
		 */

		ClientFactory factory = new ClientFactory(accesToken);

		// Step 1. Generate and Publish the Keys

		/**
		 * First a simple IP messaging chat application is generating the keys
		 * and publishing them to the Public Keys Service where they are
		 * available in open access for other users (e.g. recipient) to verify
		 * and encrypt the data for the key owner.
		 * 
		 * The following code example generates a new public/private key pair.
		 */

		String password = "jUfreBR7";
		// the private key's password is optional
		KeyPair keyPair = KeyPairGenerator.generate(password);

		/**
		 * The app is registering a Virgil Card which includes a public key and
		 * an email address identifier. The Card will be used for the public key
		 * identification and searching for it in the Public Keys Service. You
		 * can create a Virgil Card with or without identity verification
		 */
		Identity identity = new ValidatedIdentity(IdentityType.EMAIL, senderEmail);

		VirgilCardTemplate.Builder vcBuilder = new VirgilCardTemplate.Builder().setIdentity(identity)
				.setPublicKey(keyPair.getPublic());
		VirgilCard card = factory.getPublicKeyClient().createCard(vcBuilder.build(), keyPair.getPrivate());

		// Step 2. Encrypt and Sign

		/**
		 * The app is searching for all channel members' public keys on the Keys
		 * Service to encrypt a message for them. The app is signing the
		 * encrypted message with sender’s private key so that the recipient can
		 * make sure the message had been sent by the declared sender.
		 */

		String message = "Encrypt me, Please!!!";

		Builder criteriaBuilder = new Builder().setValue("recipient-test@virgilsecurity.com");
		List<VirgilCard> recipientCards = factory.getPublicKeyClient().search(criteriaBuilder.build());

		Map<String, PublicKey> recipients = new HashMap<>();
		for (VirgilCard recipientCard : recipientCards) {
			recipients.put(recipientCard.getId(), new PublicKey(recipientCard.getPublicKey().getKey()));
		}

		String encryptedMessage = CryptoHelper.encrypt(message, recipients);
		String signature = CryptoHelper.sign(encryptedMessage, keyPair.getPrivate());

		// Step 3. Send a Message

		/**
		 * The app merges the message text and the signature into one structure
		 * then serializes it to json string and sends the message to the
		 * channel using a simple IP messaging client.
		 */
		JsonObject encryptedBody = new JsonObject();
		encryptedBody.addProperty("Content", encryptedMessage);
		encryptedBody.addProperty("Signature", signature);

		// Step 4. Receive a Message

		/**
		 * An encrypted message is received on the recipient’s side using an IP
		 * messaging client.
		 * 
		 * In order to decrypt and verify the received data, the app on
		 * recipient’s side needs to get sender’s Virgil Card from the Keys
		 * Service.
		 */

		// Step 5. Verify and Decrypt

		/*
		 * The application is making sure the message came from the declared
		 * sender by getting his card on Virgil Public Keys Service. In case of
		 * success, the message is decrypted using the recipient's private key.
		 */

		PrivateKey recipientPrivateKey = new PrivateKey("{RECIPIENT_KEY}");

		String encryptedContent = encryptedBody.get("Content").getAsString();
		String encryptedContentSignature = encryptedBody.get("Signature").getAsString();
		boolean isValid = CryptoHelper.verify(encryptedContent, encryptedContentSignature,
				new PublicKey(card.getPublicKey().getKey()));
		if (!isValid) {
			throw new Exception("Signature is not valid.");
		}

		String originalMessage = CryptoHelper.decrypt(encryptedContent, "{RECIPIENT_CARD_ID}", recipientPrivateKey);
	}

}
