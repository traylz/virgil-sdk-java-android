
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
import com.virgilsecurity.sdk.client.http.ResponseCallback;
import com.virgilsecurity.sdk.client.http.VoidResponseCallback;
import com.virgilsecurity.sdk.client.model.APIError;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.Action;
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
 * This sample shows how to make asynchronous calls to Identity service.
 * 
 * @author Andrii Iakovenko
 *
 */
public class KeysSampleAsync {

	private String actionId;
	private ValidatedIdentity identity;
	VirgilCard cardInfo;
	List<VirgilCard> cards;
	List<VirgilCard> appCards;

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		KeysSampleAsync sample = new KeysSampleAsync();
		sample.run();
	}

	private void run() throws IOException, InterruptedException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String accesToken = "{ACCESS_TOKEN}";

		/*
		 * This object allows to lock main thread until asynchronous request
		 * finish. Don't use this approach in real apps!
		 */

		final Object lock = new Object();

		/** Identity Check */

		// Register an identity
		System.out.println("Enter email address: ");
		String email = br.readLine();

		ClientFactory factory = new ClientFactory(accesToken);

		// Initialize the identity verification process.
		factory.getIdentityClient().verify(IdentityType.EMAIL, email, new ResponseCallback<Action>() {

			@Override
			public void onSuccess(Action object) {
				// Verified
				actionId = object.getActionId();
				synchronized (lock) {
					lock.notify();
				}
			}

			@Override
			public void onFailure(APIError error) {
				System.out.println("Identity verification error: " + error.getMessage());
				synchronized (lock) {
					lock.notify();
				}
			}
		});

		synchronized (lock) {
			lock.wait();
		}

		// Use confirmation code sent to your email box
		System.out.println("Check your email box");
		System.out.println("Enter confirmation code: ");
		String confirmationCode = br.readLine();

		// Confirm the identity and get a temporary token
		factory.getIdentityClient().confirm(actionId, confirmationCode, new ResponseCallback<ValidatedIdentity>() {

			@Override
			public void onSuccess(ValidatedIdentity object) {
				System.out.println("Confirmed");
				identity = object;
				synchronized (lock) {
					lock.notify();
				}
			}

			@Override
			public void onFailure(APIError error) {
				System.out.println("Confirmation error: " + error.getMessage());
				synchronized (lock) {
					lock.notify();
				}
			}
		});

		synchronized (lock) {
			lock.wait();
		}

		// Validate identity
		factory.getIdentityClient().validate(identity, new VoidResponseCallback() {

			@Override
			public void onSuccess(boolean result) {

				if (result) {
					System.out.println("You identity is validated");
				} else {
					System.out.println("You identity is not validated");
				}
				synchronized (lock) {
					lock.notify();
				}
			}

			@Override
			public void onFailure(APIError error) {
				System.out.println("Validation error: " + error.getMessage());
				synchronized (lock) {
					lock.notify();
				}
			}
		});

		synchronized (lock) {
			lock.wait();
		}

		/** Cards and Public Keys */

		// Publish a Virgil Card
		KeyPair keyPair = KeyPairGenerator.generate();

		VirgilCardTemplate.Builder vcBuilder = new VirgilCardTemplate.Builder().setIdentity(identity)
				.setPublicKey(keyPair.getPublic());
		factory.getPublicKeyClient().createCard(vcBuilder.build(), keyPair.getPrivate(),
				new ResponseCallback<VirgilCard>() {

					@Override
					public void onSuccess(VirgilCard object) {
						cardInfo = object;
						synchronized (lock) {
							lock.notify();
						}
					}

					@Override
					public void onFailure(APIError error) {
						System.out.println("Can't create Virgil Card: " + error.getMessage());
						synchronized (lock) {
							lock.notify();
						}
					}
				});
		System.out.println("Created Virgil Card with ID: " + cardInfo.getId());

		// Search for Cards
		Builder criteriaBuilder = new Builder().setValue(email)
				// Created card is unconfirmed, thus we should include
				// unconfirmed cards
				.setIncludeUnauthorized(true);
		factory.getPublicKeyClient().search(criteriaBuilder.build(), new ResponseCallback<List<VirgilCard>>() {

			@Override
			public void onSuccess(List<VirgilCard> object) {
				cards = object;
				System.out.println("Virgil Cards found by criteria:");
				for (VirgilCard card : cards) {
					System.out.println(card.getId());
				}
				synchronized (lock) {
					lock.notify();
				}
			}

			@Override
			public void onFailure(APIError error) {
				System.out.println("Search error: " + error.getMessage());
				synchronized (lock) {
					lock.notify();
				}
			}

		});

		synchronized (lock) {
			lock.wait();
		}

		// Search for Application Cards
		System.out.println();
		System.out.println("Enter application ID: ");
		String appId = br.readLine();

		SearchCriteria criteria = new SearchCriteria();
		criteria.setValue(appId);

		factory.getPublicKeyClient().searchApp(criteriaBuilder.build(), new ResponseCallback<List<VirgilCard>>() {

			@Override
			public void onSuccess(List<VirgilCard> object) {
				appCards = object;
				System.out.println("Virgil Cards found by application:");
				for (VirgilCard card : appCards) {
					System.out.println(card.getId());
				}
				synchronized (lock) {
					lock.notify();
				}
			}

			@Override
			public void onFailure(APIError error) {
				System.out.println("Search error: " + error.getMessage());
				synchronized (lock) {
					lock.notify();
				}
			}

		});

		synchronized (lock) {
			lock.wait();
		}

		// Trust Virgil Card
		System.out.println("Enter signed card ID");
		String signedCardId = br.readLine();
		System.out.println("Enter signed card hash");
		String signedCardHash = br.readLine();

		factory.getPublicKeyClient().signCard(signedCardId, signedCardHash, cardInfo.getId(), keyPair.getPrivate(),
				new ResponseCallback<SignResponse>() {

					@Override
					public void onSuccess(SignResponse signResponse) {
						// Process sign response
						synchronized (lock) {
							lock.notify();
						}
					}

					@Override
					public void onFailure(APIError error) {
						System.out.println("Sign error: " + error.getMessage());
						synchronized (lock) {
							lock.notify();
						}
					}
				});

		synchronized (lock) {
			lock.wait();
		}

		// Get a Public Key
		factory.getPublicKeyClient().getKey(cardInfo.getPublicKey().getId(), new ResponseCallback<PublicKeyInfo>() {

			@Override
			public void onSuccess(PublicKeyInfo publicKey) {
				// Process public key
				synchronized (lock) {
					lock.notify();
				}
			}

			@Override
			public void onFailure(APIError error) {
				System.out.println("Get key error: " + error.getMessage());
				synchronized (lock) {
					lock.notify();
				}
			}
		});

		synchronized (lock) {
			lock.wait();
		}

		// Untrust a Virgil Card
		factory.getPublicKeyClient().unsignCard(signedCardId, cardInfo.getId(), keyPair.getPrivate(),
				new VoidResponseCallback() {

					@Override
					public void onSuccess(boolean result) {
						System.out.println("Unsign result: " + result);
						synchronized (lock) {
							lock.notify();
						}
					}

					@Override
					public void onFailure(APIError error) {
						System.out.println("Unsing error: " + error.getMessage());
						synchronized (lock) {
							lock.notify();
						}
					}
				});

		synchronized (lock) {
			lock.wait();
		}

		// Revoke a Virgil Card
		factory.getPublicKeyClient().deleteCard(identity, cardInfo.getId(), keyPair.getPrivate(),
				new VoidResponseCallback() {

					@Override
					public void onSuccess(boolean result) {
						System.out.println("Revoke public key result: " + result);
						synchronized (lock) {
							lock.notify();
						}
					}

					@Override
					public void onFailure(APIError error) {
						System.out.println("Revoke public key error: " + error.getMessage());
						synchronized (lock) {
							lock.notify();
						}
					}
				});

		synchronized (lock) {
			lock.wait();
		}

		/** Private Keys */

		// Obtain public key for the Private Keys Service retrieved from the
		// Public Keys Service
		criteria = new SearchCriteria();
		criteria.setValue("com.virgilsecurity.private-keys");
		final VirgilCard serviceCard = factory.getPublicKeyClient().searchApp(criteria).get(0);

		// Create Virgil Card because previous one was removed
		vcBuilder = new VirgilCardTemplate.Builder().setIdentity(identity).setPublicKey(keyPair.getPublic());
		cardInfo = factory.getPublicKeyClient().createCard(vcBuilder.build(), keyPair.getPrivate());

		// Stash a Private Key
		factory.getPrivateKeyClient(serviceCard).stash(cardInfo.getId(), keyPair.getPrivate(),
				new VoidResponseCallback() {

					@Override
					public void onSuccess(boolean result) {
						System.out.println("Private key stashed");
						synchronized (lock) {
							lock.notify();
						}
					}

					@Override
					public void onFailure(APIError error) {
						System.out.println("Private key not stashed: " + error.getMessage());
						synchronized (lock) {
							lock.notify();
						}
					}
				});

		synchronized (lock) {
			lock.wait();
		}

		// Get a Private Key
		actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, email);
		System.out.println("Check your email box");
		System.out.println("Enter confirmation code: ");
		confirmationCode = br.readLine();
		// use confirmation code that has been sent to you email box.
		identity = factory.getIdentityClient().confirm(actionId, confirmationCode);

		factory.getPrivateKeyClient(serviceCard).get(cardInfo.getId(), identity,
				new ResponseCallback<PrivateKeyInfo>() {

					@Override
					public void onSuccess(PrivateKeyInfo keyInfo) {
						System.out.println("Private key: " + keyInfo.getKey());
						synchronized (lock) {
							lock.notify();
						}
					}

					@Override
					public void onFailure(APIError error) {
						System.out.println("Private key not retrieved: " + error.getMessage());
						synchronized (lock) {
							lock.notify();
						}
					}
				});
		synchronized (lock) {
			lock.wait();
		}

		// Destroy a Private Key
		factory.getPrivateKeyClient(serviceCard).destroy(cardInfo.getId(), keyPair.getPrivate(),
				new VoidResponseCallback() {

					@Override
					public void onSuccess(boolean result) {
						System.out.println("Private key destroy result: " + result);
						synchronized (lock) {
							lock.notify();
						}
					}

					@Override
					public void onFailure(APIError error) {
						System.out.println("Private key not destroyed due to error: " + error.getMessage());
						synchronized (lock) {
							lock.notify();
						}
					}
				});
		synchronized (lock) {
			lock.wait();
		}
	}

}
