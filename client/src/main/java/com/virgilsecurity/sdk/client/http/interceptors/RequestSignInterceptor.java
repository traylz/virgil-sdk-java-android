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
package com.virgilsecurity.sdk.client.http.interceptors;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.virgilsecurity.sdk.client.utils.Constants;
import com.virgilsecurity.sdk.crypto.CryptoHelper;
import com.virgilsecurity.sdk.crypto.Password;
import com.virgilsecurity.sdk.crypto.PrivateKey;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

/**
 * Request sign interceptor.
 *
 * @author Andrii Iakovenko
 *
 */
public class RequestSignInterceptor implements Interceptor {

	private static final Logger LOGGER = Logger.getLogger(RequestSignInterceptor.class.getName());

	private PrivateKey privateKey;
	private Password password;

	/**
	 * Create a new instance
	 *
	 * @param privateKey
	 */
	public RequestSignInterceptor(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	/**
	 * Create a new instance
	 *
	 * @param privateKey
	 * @param password
	 */
	public RequestSignInterceptor(PrivateKey privateKey, Password password) {
		this(privateKey);

		this.password = password;
	}

	/* (non-Javadoc)
	 * @see okhttp3.Interceptor#intercept(okhttp3.Interceptor.Chain)
	 */
	@Override
	public Response intercept(Chain chain) throws IOException {
		Request original = chain.request();

		// Generate request ID
		String requestId = UUID.randomUUID().toString();
		Request.Builder requestBuilder = original.newBuilder().header(Constants.Header.X_VIRGIL_REQUEST_ID, requestId);

		if (privateKey != null) {
			// Get request body as string
			Buffer requestBody = new Buffer();
			original.body().writeTo(requestBody);

			// Build request text
			String requestText = requestId + requestBody.readUtf8();

			// Sign request text with private key
			try {
				String sign = null;
				if (password == null) {
					sign = CryptoHelper.sign(requestText, privateKey);
				} else {
					sign = CryptoHelper.sign(requestText, privateKey, password);
				}
				requestBuilder.header(Constants.Header.X_VIRGIL_REQUEST_SIGN, sign);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Can't sign request text", e);
			}
		}

		requestBuilder.method(original.method(), original.body());

		Request request = requestBuilder.build();
		return chain.proceed(request);
	}

}
