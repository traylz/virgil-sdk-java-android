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
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.virgilsecurity.sdk.client.exceptions.ServiceException;
import com.virgilsecurity.sdk.client.model.APIError;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This abstract class implements common functionality for all clients.
 *
 * @author Andrii Iakovenko
 *
 */
public abstract class AbstractClient {

	protected OkHttpClient.Builder httpClient;

	protected Retrofit.Builder builder;

	protected String accessToken;

	/**
	 * Create a new instance.
	 *
	 * @param baseUrl
	 *            base URL of API.
	 * @param accessToken
	 *            access token.
	 */
	public AbstractClient(String baseUrl, String accessToken) {
		this.accessToken = accessToken;

		httpClient = new OkHttpClient.Builder();
		httpClient.readTimeout(60, TimeUnit.SECONDS);
		httpClient.connectTimeout(60, TimeUnit.SECONDS);

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
		builder = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create(gson));
	}

	public <S> S createService(Class<S> serviceClass) {
		Retrofit retrofit = builder.client(httpClient.build()).build();
		return retrofit.create(serviceClass);
	}

	/**
	 * Handle response from Virgil service.
	 * 
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public Object handleResponse(Response<?> response) throws IOException {
		if (response.isSuccessful()) {
			return response.body();
		} else {
			APIError error = new APIError(response.code(), response.errorBody().string());
			throw new ServiceException(error);
		}
	}

}
