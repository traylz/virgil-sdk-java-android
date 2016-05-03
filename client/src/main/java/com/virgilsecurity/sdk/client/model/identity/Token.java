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
package com.virgilsecurity.sdk.client.model.identity;

import com.google.gson.annotations.SerializedName;

/**
 * This class represent the token.
 * 
 * @author Andrii Iakovenko
 */
public class Token {

	@SerializedName("time_to_live")
	private Integer timeToLive;

	@SerializedName("count_to_live")
	private Integer countToLive;

	/**
	 * Create a new instance of {@code Token}
	 *
	 */
	public Token() {
	}

	/**
	 * Create a new instance of {@code Token}
	 *
	 * @param timeToLive
	 *            token's time to live.
	 * @param countToLive
	 *            token's count to live.
	 */
	public Token(Integer timeToLive, Integer countToLive) {
		this.timeToLive = timeToLive;
		this.countToLive = countToLive;
	}

	/**
	 * Returns token's time to live.
	 * 
	 * @return the token's time to live.
	 */
	public Integer getTimeToLive() {
		return timeToLive;
	}

	/**
	 * Sets the token's time to live.
	 * 
	 * @param timeToLive
	 *            the token's time to live.
	 */
	public void setTimeToLive(Integer timeToLive) {
		this.timeToLive = timeToLive;
	}

	/**
	 * Returns the token's count to live.
	 * 
	 * @return the token's count to live.
	 */
	public Integer getCountToLive() {
		return countToLive;
	}

	/**
	 * Sets the token's count to live.
	 * 
	 * @param countToLive
	 *            the token's count to live.
	 */
	public void setCountToLive(Integer countToLive) {
		this.countToLive = countToLive;
	}

}
