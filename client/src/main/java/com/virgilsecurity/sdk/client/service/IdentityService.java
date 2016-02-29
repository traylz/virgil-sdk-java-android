/*
 * Copyright (c) 2015, Virgil Security, Inc.
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
package com.virgilsecurity.sdk.client.service;

import com.virgilsecurity.sdk.client.model.Identity;
import com.virgilsecurity.sdk.client.model.identity.Action;
import com.virgilsecurity.sdk.client.model.identity.Confirmation;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * This interface represents Virgil Identity HTTP API.
 * 
 * @author Andrii Iakovenko
 */
public interface IdentityService {

	/**
	 * Initiates the process to verify the Identity Request info.
	 * 
	 * @param identity
	 *            the identity for verification.
	 * @return
	 * 
	 * @see Identity
	 * @see Action
	 * @see retrofit2.Call
	 */
	@POST("/v1/verify")
	Call<Action> verify(@Body Identity identity);

	/**
	 * Confirms identity from the /verify step to obtain an identity
	 * confirmation token Request info.
	 * 
	 * @param confirmation
	 *            the confirmation data.
	 * @return
	 * 
	 * @see Confirmation
	 * @see ValidatedIdentity
	 * @see retrofit2.Call
	 */
	@POST("/v1/confirm")
	Call<ValidatedIdentity> confirm(@Body Confirmation confirmation);

	/**
	 * Validates the passed token.
	 * 
	 * @param identity
	 *            the identity for validation. The {@code token} should be
	 *            defined.
	 * @return
	 * 
	 * @see Confirmation
	 * @see ValidatedIdentity
	 */
	@POST("/v1/validate")
	Call<Void> validate(@Body ValidatedIdentity identity);

}
