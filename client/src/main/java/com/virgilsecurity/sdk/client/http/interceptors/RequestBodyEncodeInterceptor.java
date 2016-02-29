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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.virgilsecurity.sdk.crypto.CryptoHelper;
import com.virgilsecurity.sdk.crypto.PublicKey;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * This request intercepter encodes request body with a Virgil Card.
 *
 * @author Andrii Iakovenko
 *
 */
public class RequestBodyEncodeInterceptor implements Interceptor {

	private static final Logger LOGGER = Logger.getLogger(RequestBodyEncodeInterceptor.class.getName());

	private String recipientId;
	private PublicKey publicKey;

	/**
	 * Create a new instance of {@code RequestBodyEncodeInterceptor}
	 *
	 * @param recipientId
	 * @param publicKey
	 */
	public RequestBodyEncodeInterceptor(String recipientId, PublicKey publicKey) {
		this.recipientId = recipientId;
		this.publicKey = publicKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okhttp3.Interceptor#intercept(okhttp3.Interceptor.Chain)
	 */
	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();

		// Encode request body
		try {
			RequestBody body = encodeBody(request.body());

			Request.Builder builder = request.newBuilder();
			request = builder.method(request.method(), body).build();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Can't encode request body", e);
		}

		return chain.proceed(request);
	}

	/**
	 * Encode request body.
	 * 
	 * @param originalBody
	 *            the original request body.
	 * @return the encoded request body.
	 * @throws Exception
	 */
	private RequestBody encodeBody(RequestBody originalBody) throws Exception {
		// Get request body as bytes array
		Buffer buffer = new Buffer();
		originalBody.writeTo(buffer);
		byte[] body = new byte[(int) buffer.size()];
		buffer.read(body);

		String encryptedBody = CryptoHelper.encrypt(new String(body), recipientId, publicKey);
		RequestBody requestBody = RequestBody.create(originalBody.contentType(), encryptedBody);
		return requestBody;
	}

}
