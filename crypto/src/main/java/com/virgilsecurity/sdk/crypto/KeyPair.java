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
package com.virgilsecurity.sdk.crypto;

import com.virgilsecurity.crypto.VirgilKeyPair;

/**
 * This class is a holder for a key pair (a public key and a private key).
 *
 * @see PublicKey
 * @see PrivateKey
 *
 * @author Andrii Iakovenko
 */
public class KeyPair implements java.io.Serializable {

	private static final long serialVersionUID = 254748684016090131L;
	private final PrivateKey privateKey;
	private final PublicKey publicKey;

	/**
	 * Constructs a key pair from the given public key and private key.
	 *
	 * @param publicKey
	 *            the public key.
	 * @param privateKey
	 *            the private key.
	 */
	public KeyPair(PublicKey publicKey, PrivateKey privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	/**
	 * Constructs a key pair from the given VirginKeyPair.
	 *
	 * @param keyPair
	 *            the Virgin key pair.
	 */
	public KeyPair(VirgilKeyPair keyPair) {
		this.publicKey = new PublicKey(keyPair.publicKey());
		this.privateKey = new PrivateKey(keyPair.privateKey());
	}

	/**
	 * Returns a reference to the public key component of this key pair.
	 *
	 * @return a reference to the public key.
	 */
	public PublicKey getPublic() {
		return publicKey;
	}

	/**
	 * Returns a reference to the private key component of this key pair.
	 *
	 * @return a reference to the private key.
	 */
	public PrivateKey getPrivate() {
		return privateKey;
	}

}
