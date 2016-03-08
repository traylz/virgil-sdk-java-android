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
import java.util.UUID;

import com.virgilsecurity.sdk.client.exceptions.ServiceException;
import com.virgilsecurity.sdk.client.http.ResponseCallback;
import com.virgilsecurity.sdk.client.http.VoidResponseCallback;
import com.virgilsecurity.sdk.client.http.interceptors.RequestBodyEncodeInterceptor;
import com.virgilsecurity.sdk.client.http.interceptors.RequestSignInterceptor;
import com.virgilsecurity.sdk.client.http.interceptors.ResponseBodyDecodeInterceptor;
import com.virgilsecurity.sdk.client.http.interceptors.TokenInterceptor;
import com.virgilsecurity.sdk.client.model.APIError;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.privatekey.PrivateKeyAPIError;
import com.virgilsecurity.sdk.client.model.privatekey.PrivateKeyInfo;
import com.virgilsecurity.sdk.client.model.privatekey.PrivateKeyRequestPayload;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.service.PrivateKeyService;
import com.virgilsecurity.sdk.crypto.Base64;
import com.virgilsecurity.sdk.crypto.Password;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Private Key service client.
 *
 * @author Andrii Iakovenko
 *
 */
public class PrivateKeyClient extends AbstractClient {

	public static final String PRIVATE_KEY_BASE_URL = "https://keys-private.virgilsecurity.com";

	private VirgilCard serviceCard;

	/**
	 * Create a new instance of {@code PrivateKeyClient}
	 *
	 * @param accessToken
	 *            the access token.
	 * @param serviceCard
	 *            the the service's Virgil Card
	 */
	public PrivateKeyClient(String accessToken, VirgilCard serviceCard) {
		this(PRIVATE_KEY_BASE_URL, accessToken, serviceCard);
	}

	/**
	 * Create a new instance of {@code PrivateKeyClient}
	 *
	 * @param baseUrl
	 *            the base URL of Virgil Keys endpoint.
	 * @param accessToken
	 *            the access token.
	 * @param serviceCard
	 *            the the service's Virgil Card
	 */
	public PrivateKeyClient(String baseUrl, String accessToken, VirgilCard serviceCard) {
		super(baseUrl, accessToken);

		if (serviceCard == null) {
			throw new IllegalArgumentException("Service Card should be defined.");
		}

		this.serviceCard = serviceCard;
	}

	/**
	 * Create new instance of Retrofit service.
	 * 
	 * @param serviceClass
	 *            Retrofit service class
	 * @param privateKey
	 *            the {@code PrivateKey} used for request signing
	 * @param privateKeyPassword
	 *            the {@code Password} used for {@code PrivateKey} protection
	 * @param responsePassword
	 *            the Password to encrypt server response. Up to 31 characters.
	 * @return created instance of service
	 * 
	 * @see PrivateKey
	 * @see Password
	 */
	public <S> S createService(Class<S> serviceClass, PrivateKey privateKey, Password privateKeyPassword,
			Password responsePassword) {
		OkHttpClient.Builder buider = new OkHttpClient.Builder();

		buider.addInterceptor(new TokenInterceptor(accessToken));

		if (privateKey != null) {
			buider.addInterceptor(new RequestSignInterceptor(privateKey, privateKeyPassword));
		}

		buider.addInterceptor(new RequestBodyEncodeInterceptor(serviceCard.getId(),
				new PublicKey(Base64.decode(serviceCard.getPublicKey().getKey()))));

		if (responsePassword != null) {
			buider.addInterceptor(new ResponseBodyDecodeInterceptor(responsePassword));
		}

		Retrofit retrofit = builder.client(buider.build()).build();
		return retrofit.create(serviceClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.virgilsecurity.sdk.client.AbstractClient#handleResponse(retrofit2.
	 * Response)
	 */
	@Override
	public Object handleResponse(Response<?> response) throws IOException {
		if (response.isSuccess()) {
			return response.body();
		} else {
			APIError error = new PrivateKeyAPIError(response.code(), response.errorBody().string());
			throw new ServiceException(error);
		}
	}

	/**
	 * Uploads private key to private key store.
	 * 
	 * @param virgilCardId
	 *            The public key identifier.
	 * @param privateKey
	 *            The private key value. Private key is used to produce sign. It
	 *            is not transfered over network.
	 */
	public void stash(String virgilCardId, PrivateKey privateKey) {
		stash(virgilCardId, privateKey, (Password) null);
	}

	/**
	 * Uploads private key to private key store.
	 * 
	 * @param virgilCardId
	 *            The public key identifier.
	 * @param privateKey
	 *            The private key value. Private key is used to produce sign. It
	 *            is not transfered over network.
	 * @param password
	 *            The private key password.
	 */
	public void stash(String virgilCardId, PrivateKey privateKey, Password password) {
		try {
			PrivateKeyInfo info = new PrivateKeyInfo();
			info.setCardId(virgilCardId);
			info.setKey(privateKey.getAsBase64String());

			Response<Void> response = createService(PrivateKeyService.class, privateKey, password, null).stash(info)
					.execute();
			handleResponse(response);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Uploads private key to private key store.
	 * 
	 * @param virgilCardId
	 *            The public key identifier.
	 * @param privateKey
	 *            The private key value. Private key is used to produce sign. It
	 *            is not transfered over network.
	 * @param callback
	 */
	public void stash(String virgilCardId, PrivateKey privateKey, VoidResponseCallback callback) {
		stash(virgilCardId, privateKey, null, callback);
	}

	/**
	 * Uploads private key to private key store.
	 * 
	 * @param virgilCardId
	 *            the public key identifier.
	 * @param privateKey
	 *            the private key value. Private key is used to produce sign. It
	 *            is not transfered over network.
	 * @param password
	 *            the private key password.
	 * @param callback
	 */
	public void stash(String virgilCardId, PrivateKey privateKey, Password password, VoidResponseCallback callback) {
		PrivateKeyInfo info = new PrivateKeyInfo();
		info.setCardId(virgilCardId);
		info.setKey(privateKey.getAsBase64String());

		createService(PrivateKeyService.class, privateKey, password, null).stash(info).enqueue(callback);
	}

	/**
	 * Downloads private part of key by its public id.
	 * 
	 * @param virgilCardId
	 *            the public key identifier.
	 * @param identity
	 *            the validated identity token with.
	 * @return
	 */
	public PrivateKeyInfo get(String virgilCardId, ValidatedIdentity identity) {
		try {
			String responsePassword = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 31);

			PrivateKeyRequestPayload payload = new PrivateKeyRequestPayload();
			payload.setCardId(virgilCardId);
			payload.setIdentity(identity);
			payload.setResponsePassword(responsePassword);

			Response<PrivateKeyInfo> response = createService(PrivateKeyService.class, null, null,
					new Password(responsePassword)).get(payload).execute();
			return (PrivateKeyInfo) handleResponse(response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	/**
	 * Downloads private part of key by its public id.
	 * 
	 * @param virgilCardId
	 *            the public key identifier.
	 * @param identity
	 *            the validated identity token with.
	 */
	public void get(String virgilCardId, ValidatedIdentity identity, ResponseCallback<PrivateKeyInfo> callback) {
		String responsePassword = UUID.randomUUID().toString().replaceAll("-", "");

		PrivateKeyRequestPayload payload = new PrivateKeyRequestPayload();
		payload.setCardId(virgilCardId);
		payload.setIdentity(identity);
		payload.setResponsePassword(responsePassword);

		createService(PrivateKeyService.class, null, null, new Password(responsePassword)).get(payload)
				.enqueue(callback);
	}

	/**
	 * Deletes the private key from service by specified card ID.
	 * 
	 * @param virgilCardId
	 *            The public key identifier.
	 * @param privateKey
	 *            The private key value. Private key is used to produce sign. It
	 *            is not transfered over network.
	 */
	public void destroy(String virgilCardId, PrivateKey privateKey) {
		destroy(virgilCardId, privateKey, (Password) null);
	}

	/**
	 * Deletes the private key from service by specified card ID.
	 * 
	 * @param virgilCardId
	 *            The public key identifier.
	 * @param privateKey
	 *            The private key value. Private key is used to produce sign. It
	 *            is not transfered over network.
	 * @param password
	 *            The private key password.
	 */
	public void destroy(String virgilCardId, PrivateKey privateKey, Password password) {
		try {
			PrivateKeyInfo info = new PrivateKeyInfo();
			info.setCardId(virgilCardId);

			Response<Void> response = createService(PrivateKeyService.class, privateKey, password, null).destroy(info)
					.execute();
			handleResponse(response);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Deletes the private key from service by specified card ID.
	 * 
	 * @param virgilCardId
	 *            the public key identifier.
	 * @param privateKey
	 *            the private key value. Private key is used to produce sign. It
	 *            is not transfered over network.
	 * @param callback
	 */
	public void destroy(String virgilCardId, PrivateKey privateKey, VoidResponseCallback callback) {
		destroy(virgilCardId, privateKey, (Password) null, callback);
	}

	/**
	 * Deletes the private key from service by specified card ID.
	 * 
	 * @param virgilCardId
	 *            the public key identifier.
	 * @param privateKey
	 *            the private key value. Private key is used to produce sign. It
	 *            is not transfered over network.
	 * @param password
	 *            the private key password.
	 * @param callback
	 */
	public void destroy(String virgilCardId, PrivateKey privateKey, Password password, VoidResponseCallback callback) {
		PrivateKeyInfo info = new PrivateKeyInfo();
		info.setCardId(virgilCardId);

		createService(PrivateKeyService.class, privateKey, password, null).destroy(info).enqueue(callback);
	}

}
