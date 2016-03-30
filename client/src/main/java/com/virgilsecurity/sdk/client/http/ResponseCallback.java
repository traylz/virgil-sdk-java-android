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
package com.virgilsecurity.sdk.client.http;

import java.io.IOException;

import com.virgilsecurity.sdk.client.model.APIError;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The {@code Callback} which called by response from service.
 *
 * @author Andrii Iakovenko
 *
 */
public abstract class ResponseCallback<T> implements Callback<T> {
	
	/* (non-Javadoc)
	 * @see retrofit2.Callback#onResponse(retrofit2.Call, retrofit2.Response)
	 */
	@Override
	public void onResponse(Call<T> call, Response<T> response) {
		if (response.code() < 400) {
			onSuccess(response.body());
		} else {
			String body = "";
			try {
				body = response.errorBody().string();
			} catch (IOException e) {
				// nothing to do
			}
			onFailure(new APIError(response.code(), body));
		}
	}
	
	/* (non-Javadoc)
	 * @see retrofit2.Callback#onFailure(retrofit2.Call, java.lang.Throwable)
	 */
	@Override
	public void onFailure(Call<T> call, Throwable t) {
		onFailure(new APIError(t));
	}

	/**
	 * This method called if request to HTTP API successful.
	 * 
	 * @param object
	 *            response data.
	 */
	public abstract void onSuccess(T object);

	/**
	 * This method called if request to HTTP API failed due to some reason.
	 * 
	 * @param error
	 *            the error identified.
	 * 
	 * @see APIError
	 */
	public abstract void onFailure(APIError error);

}
