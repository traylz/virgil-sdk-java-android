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
package com.virgilsecurity.sdk.highlevel;

import java.util.ArrayList;
import java.util.List;

import com.virgilsecurity.sdk.client.utils.ConvertionUtils;
import com.virgilsecurity.sdk.client.utils.StringUtils;
import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.PublicKey;
import com.virgilsecurity.sdk.crypto.exceptions.EmptyArgumentException;

/**
 * This class represents list of Virgil Cards.
 *
 * @author Andrii Iakovenko
 *
 */
public class VirgilCards extends ArrayList<VirgilCard> {

	private static final long serialVersionUID = -851151724028601817L;

	/**
	 * Encrypts the text.
	 * 
	 * @param text
	 *            The text to encrypt.
	 * @return The encrypted data.
	 * 
	 * @throws EmptyArgumentException
	 *             if text is blank.
	 */
	public byte[] encryptText(String text) {
		if (StringUtils.isBlank(text)) {
			throw new EmptyArgumentException("text");
		}

		return encrypt(ConvertionUtils.toBytes(text));
	}

	/**
	 * Encrypts the data.
	 * 
	 * @param data
	 *            The data to encrypt.
	 * @return The encrypted data.
	 * 
	 * @throws EmptyArgumentException if there is no recipients.
	 */
	public byte[] encrypt(byte[] data) {
		if (this.isEmpty()) {
			throw new EmptyArgumentException("recipients");
		}

		Crypto crypto = VirgilConfig.getService(Crypto.class);
		List<PublicKey> publicKeys = new ArrayList<>();
		for (VirgilCard recipient : this) {
			publicKeys.add(crypto.importPublicKey(recipient.getPublicKey()));
		}

		byte[] cipherdata = crypto.encrypt(data, publicKeys.toArray(new PublicKey[0]));

		return cipherdata;
	}

}
