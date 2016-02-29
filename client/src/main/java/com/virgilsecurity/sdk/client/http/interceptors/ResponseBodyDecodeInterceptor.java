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
import com.virgilsecurity.sdk.crypto.Password;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * This response intercepter decodes response with a password.
 *
 * @author Andrii Iakovenko
 *
 */
public class ResponseBodyDecodeInterceptor implements Interceptor {

	private static final Logger LOGGER = Logger.getLogger(ResponseBodyDecodeInterceptor.class.getName());

	private Password password;

	/**
	 * Create a new instance of {@code ResponseBodyDecodeInterceptor}
	 *
	 * @param password
	 *            the Password to encrypt server response. Up to 31 characters.
	 */
	public ResponseBodyDecodeInterceptor(Password password) {
		this.password = password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okhttp3.Interceptor#intercept(okhttp3.Interceptor.Chain)
	 */
	@Override
	public Response intercept(Chain chain) throws IOException {
		Response response = chain.proceed(chain.request());
		if (response.code() < 400) {
			try {
				ResponseBody body = decodeBody(response.body());

				Response.Builder builder = response.newBuilder();
				response = builder.body(body).build();
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Can't encode request body", e);
			}
		}

		return response;
	}

	/**
	 * Decode response body.
	 * 
	 * @param encodedBody
	 *            the encoded response body.
	 * @return the decoded response body.
	 * @throws Exception
	 */
	private ResponseBody decodeBody(ResponseBody encodedBody) throws Exception {
		String text = CryptoHelper.decrypt(encodedBody.string(), password.toString());
		ResponseBody responseBody = ResponseBody.create(encodedBody.contentType(), text);
		return responseBody;
	}

}
