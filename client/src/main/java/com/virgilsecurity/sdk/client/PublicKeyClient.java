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

import java.io.IOException;
import java.util.List;

import com.virgilsecurity.sdk.client.exceptions.ServiceException;
import com.virgilsecurity.sdk.client.http.ResponseCallback;
import com.virgilsecurity.sdk.client.http.VoidResponseCallback;
import com.virgilsecurity.sdk.client.http.interceptors.RequestSignInterceptor;
import com.virgilsecurity.sdk.client.http.interceptors.TokenInterceptor;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.publickey.DeleteRequest;
import com.virgilsecurity.sdk.client.model.publickey.Identities;
import com.virgilsecurity.sdk.client.model.publickey.PublicKeyInfo;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.client.service.PublicKeyService;
import com.virgilsecurity.sdk.crypto.Password;
import com.virgilsecurity.sdk.crypto.PrivateKey;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Public Key service client.
 *
 * @author Andrii Iakovenko
 *
 */
public class PublicKeyClient extends AbstractClient {

	public static final String PUBLIC_KEY_BASE_URL = "https://keys.virgilsecurity.com";

	/**
	 * Create a new instance of PublicKeyClient.
	 *
	 * @param accessToken
	 *            the access token.
	 */
	public PublicKeyClient(String accessToken) {
		this(PUBLIC_KEY_BASE_URL, accessToken);
	}

	/**
	 * Create a new instance of PublicKeyClient.
	 *
	 * @param baseUrl
	 *            the base URL of Virgil Keys endpoint.
	 * @param accessToken
	 *            the access token.
	 */
	public PublicKeyClient(String baseUrl, String accessToken) {
		super(baseUrl, accessToken);
	}

	/**
	 * Create new instance of Retrofit service.
	 * 
	 * @param serviceClass
	 *            Retrofit service class
	 * @return created instance of service
	 */
	public <S> S createService(Class<S> serviceClass) {
		return createService(serviceClass, null, null);
	}

	/**
	 * Create new instance of Retrofit service.
	 * 
	 * @param serviceClass
	 *            Retrofit service class
	 * @param privateKey
	 *            the {@code PrivateKey} used for request signing
	 * @return created instance of service
	 * 
	 * @see PrivateKey
	 */
	public <S> S createService(Class<S> serviceClass, PrivateKey privateKey) {
		return createService(serviceClass, privateKey, null);
	}

	/**
	 * Create new instance of Retrofit service.
	 * 
	 * @param serviceClass
	 *            Retrofit service class
	 * @param privateKey
	 *            the {@code PrivateKey} used for request signing
	 * @param password
	 *            the {@code Password} used for {@code PrivateKey} protection
	 * @return created instance of service
	 * 
	 * @see PrivateKey
	 * @see Password
	 */
	public <S> S createService(Class<S> serviceClass, PrivateKey privateKey, Password password) {
		OkHttpClient.Builder buider = new OkHttpClient.Builder();

		if (accessToken != null) {
			buider.addInterceptor(new TokenInterceptor(accessToken));
		}

		if (privateKey != null) {
			buider.addInterceptor(new RequestSignInterceptor(privateKey, password));
		}
		Retrofit retrofit = builder.client(buider.build()).build();
		return retrofit.create(serviceClass);
	}

	/**
	 * Create new Virgil Card asynchronously.
	 * 
	 * @param template
	 *            the template of created Virgil Card
	 * @param privateKey
	 *            the {@code PrivateKey} used for request signing
	 * @param callback
	 */
	public void createCard(VirgilCardTemplate template, PrivateKey privateKey, ResponseCallback<VirgilCard> callback) {
		createService(PublicKeyService.class, privateKey).createCard(template).enqueue(callback);
	}

	/**
	 * Create new Virgil Card asynchronously.
	 * 
	 * @param template
	 *            the template of created Virgil Card
	 * @param privateKey
	 *            the {@code PrivateKey} used for request signing
	 * @param password
	 *            the {@code Password} used for {@code PrivateKey} protection
	 * @param callback
	 */
	public void createCard(VirgilCardTemplate template, PrivateKey privateKey, Password password,
			ResponseCallback<VirgilCard> callback) {
		createService(PublicKeyService.class, privateKey, password).createCard(template).enqueue(callback);
	}

	/**
	 * Create new Virgil Card.
	 * 
	 * @param template
	 *            the template of created Virgil Card
	 * @param privateKey
	 *            the {@code PrivateKey} used for request signing
	 * @return
	 */
	public VirgilCard createCard(VirgilCardTemplate template, PrivateKey privateKey) {
		return createCard(template, privateKey, (Password) null);
	}

	/**
	 * Create new Virgil Card.
	 * 
	 * @param template
	 *            the template of created Virgil Card
	 * @param privateKey
	 *            the {@code PrivateKey} used for request signing
	 * @param password
	 *            the {@code Password} used for {@code PrivateKey} protection
	 * @param password
	 * @return
	 */
	public VirgilCard createCard(VirgilCardTemplate template, PrivateKey privateKey, Password password) {

		try {
			Response<VirgilCard> response = createService(PublicKeyService.class, privateKey, password)
					.createCard(template).execute();
			return ((VirgilCard) handleResponse(response));
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Revoke a Public Key endpoint. To revoke the Public Key it's mandatory to
	 * pass validation tokens obtained on Virgil Identity service for all
	 * confirmed Virgil Cards for this Public Key.
	 * 
	 * @param publicKeyId
	 *            the Public Key's ID.
	 * @param identities
	 * @param callback
	 * @throws IOException
	 */
	// TODO
	public void deleteKey(String publicKeyId, List<ValidatedIdentity> identities, VoidResponseCallback callback)
			throws IOException {
		Identities ids = new Identities();
		ids.setIdentities(identities);
		createService(PublicKeyService.class, null).deleteKey(publicKeyId, ids).enqueue(callback);
	}

	/**
	 * Returns the information about the Virgil Card by the ID.
	 * 
	 * @param cardId
	 *            the Virgil Card's ID.
	 */
	public VirgilCard getCard(String cardId) {
		try {
			Response<VirgilCard> response = createService(PublicKeyService.class).getCard(cardId).execute();
			return ((VirgilCard) handleResponse(response));
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Returns the information about the Virgil Card by the ID.
	 * 
	 * @param cardId
	 *            the Virgil Card's ID.
	 * @param callback
	 */
	public void getCard(String cardId, ResponseCallback<VirgilCard> callback) {
		createService(PublicKeyService.class, null).getCard(cardId).enqueue(callback);
	}

	/**
	 * Returns the information about the Public Key by the ID.
	 * 
	 * @param publicKeyId
	 *            the Public Key's ID.
	 */
	public PublicKeyInfo getKey(String publicKeyId) {
		try {
			Response<PublicKeyInfo> response = createService(PublicKeyService.class).getKey(publicKeyId).execute();
			return (PublicKeyInfo) handleResponse(response);
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Returns the information about the Public Key by the ID.
	 * 
	 * @param publicKeyId
	 *            the Public Key's ID.
	 * @param callback
	 */
	public void getKey(String publicKeyId, ResponseCallback<PublicKeyInfo> callback) {
		createService(PublicKeyService.class).getKey(publicKeyId).enqueue(callback);
	}

	/**
	 * Performs the search by search criteria.
	 * 
	 * @param searchCriteria
	 *            the criteria to search
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<VirgilCard> search(SearchCriteria searchCriteria) {

		try {
			Call<List<VirgilCard>> call = null;
			PublicKeyService service = createService(PublicKeyService.class);

			if (IdentityType.EMAIL.equals(searchCriteria.getType())) {
				call = service.searchEmail(searchCriteria);
			} else if (IdentityType.APPLICATION.equals(searchCriteria.getType())) {
				call = service.searchApp(searchCriteria);
			} else {
				call = service.search(searchCriteria);
			}

			Response<List<VirgilCard>> response = call.execute();
			return ((List<VirgilCard>) handleResponse(response));
		} catch (IOException e) {
			throw new ServiceException(e);
		}

	}

	/**
	 * Performs the asynchronous search by search criteria.
	 * 
	 * @param searchCriteria
	 *            the criteria to search
	 * @param callback
	 */
	public void search(SearchCriteria searchCriteria, ResponseCallback<List<VirgilCard>> callback) {
		Call<List<VirgilCard>> call = null;
		PublicKeyService service = createService(PublicKeyService.class);

		if (IdentityType.EMAIL.equals(searchCriteria.getType())) {
			call = service.searchEmail(searchCriteria);
		} else if (IdentityType.APPLICATION.equals(searchCriteria.getType())) {
			call = service.searchApp(searchCriteria);
		} else {
			call = service.search(searchCriteria);
		}

		call.enqueue(callback);
	}

	/**
	 * Revoke a Virgil Card endpoint.
	 * 
	 * @param identity
	 * @param signerCardId
	 * @param privateKey
	 *            the {@code PrivateKey} used for request signing
	 */
	public void deleteCard(ValidatedIdentity identity, String signerCardId, PrivateKey privateKey) {
		deleteCard(identity, signerCardId, privateKey, (Password) null);
	}

	/**
	 * Revoke a Virgil Card endpoint.
	 * 
	 * @param identity
	 * @param cardId
	 * @param privateKey
	 *            the {@code PrivateKey} used for request signing
	 * @param password
	 *            the {@code Password} used for {@code PrivateKey} protection
	 */
	public void deleteCard(ValidatedIdentity identity, String cardId, PrivateKey privateKey, Password password) {
		try {
			DeleteRequest request = new DeleteRequest();
			request.setIdentity(identity);

			Response<Void> response = createService(PublicKeyService.class, privateKey, password)
					.delete(cardId, cardId, request).execute();
			handleResponse(response);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Revoke a Virgil Card endpoint.
	 * 
	 * @param identity
	 * @param cardId
	 * @param privateKey
	 *            the {@code PrivateKey} used for request signing
	 * @param callback
	 */
	public void deleteCard(ValidatedIdentity identity, String cardId, PrivateKey privateKey,
			VoidResponseCallback callback) {
		deleteCard(identity, cardId, privateKey, (Password) null, callback);
	}

	/**
	 * Revoke a Virgil Card endpoint.
	 * 
	 * @param identity
	 * @param cardId
	 * @param privateKey
	 *            the {@code PrivateKey} used for request signing
	 * @param password
	 *            the {@code Password} used for {@code PrivateKey} protection
	 * @param callback
	 */
	public void deleteCard(ValidatedIdentity identity, String cardId, PrivateKey privateKey, Password password,
			VoidResponseCallback callback) {
		DeleteRequest request = new DeleteRequest();
		request.setIdentity(identity);

		createService(PublicKeyService.class, privateKey, password).delete(cardId, cardId, request).enqueue(callback);
	}

}
