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

import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.exceptions.ServiceException;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;

/**
 * This sample shows how to create unconfirmed Virgil Card.
 *
 * @author Andrii Iakovenko
 *
 */
public class CreateUnconfirmedCard {

	public static void main(String[] args) {
		String email = "{EMAIL_ADDRESS}";
		String accesToken = "{ACCESS_TOKEN}";

		ClientFactory factory = new ClientFactory(accesToken);

		ValidatedIdentity identity = new ValidatedIdentity(IdentityType.EMAIL, email);

		KeyPair keyPair = KeyPairGenerator.generate();
		VirgilCardTemplate.Builder vcBuilder = new VirgilCardTemplate.Builder().setIdentity(identity)
				.setPublicKey(keyPair.getPublic());

		try {
			VirgilCard cardInfo = factory.getPublicKeyClient().createCard(vcBuilder.build(), keyPair.getPrivate());
			System.out.println("Created Virgil Card with ID: " + cardInfo.getId());
		} catch (ServiceException e) {
			System.out.println("An error occurred: " + e.getError());
		}
	}

}
