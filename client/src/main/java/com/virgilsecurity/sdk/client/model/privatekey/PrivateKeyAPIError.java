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
package com.virgilsecurity.sdk.client.model.privatekey;

import com.virgilsecurity.sdk.client.model.APIError;

/**
 * The {@code PrivateKeyAPIError} class is the class for errors which can be
 * occurred on Virgil Private Kety services call.
 *
 * @author Andrii Iakovenko
 *
 */
public class PrivateKeyAPIError extends APIError {

	/**
	 * Create a new instance of {@code PrivateKeyAPIError}.
	 *
	 * @param statusCode
	 *            the status code of response from Virgil service.
	 * @param errorBody
	 *            the body of response from Virgil service. Virgil service
	 *            provides detailed description of error as JSON.
	 */
	public PrivateKeyAPIError(int statusCode, String errorBody) {
		super(statusCode, errorBody);
	}

	/**
	 * Create a new instance of {@code PrivateKeyAPIError}.
	 *
	 * @param throwable
	 *            the cause of an error.
	 */
	public PrivateKeyAPIError(Throwable throwable) {
		super(throwable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.virgilsecurity.sdk.client.model.APIError#getErrorPrefix()
	 */
	@Override
	protected String getErrorPrefix() {
		return "prKeyError";
	}

}
