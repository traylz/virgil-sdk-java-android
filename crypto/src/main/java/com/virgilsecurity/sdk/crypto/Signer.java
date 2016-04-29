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

import com.virgilsecurity.crypto.VirgilHash;
import com.virgilsecurity.crypto.VirgilSigner;

/**
 *
 * Wrapper on VirgilSigner.
 *
 * @author Andrii Iakovenko
 */
public class Signer implements java.lang.AutoCloseable {

	private final VirgilSigner signer;

	/**
	 * Create new instance.
	 */
	public Signer() {
		signer = new VirgilSigner();
	}

	/**
	 * Create new version.
	 *
	 * @param hash
	 */
	public Signer(VirgilHash hash) {
		signer = new VirgilSigner(hash);
	}

	/**
	 * Sign data with private key.
	 *
	 * @see PrivateKey
	 *
	 * @param data
	 *            the data to be signed.
	 * @param privateKey
	 *            the private key.
	 * @return signed data.
	 */
	public byte[] sign(byte[] data, PrivateKey privateKey) {
		return signer.sign(data, privateKey.getEncoded());
	}

	/**
	 * Sign data with private key protected with password.
	 *
	 * @see PrivateKey
	 * @see Password
	 *
	 * @param data
	 *            the data to be signed.
	 * @param privateKey
	 *            the private key.
	 * @param privateKeyPassword
	 *            the private key password.
	 * @return signed data.
	 */
	public byte[] sign(byte[] data, PrivateKey privateKey, Password privateKeyPassword) {
		return signer.sign(data, privateKey.getEncoded(), privateKeyPassword.getEncoded());
	}

	/**
	 * Verify data with signature.
	 *
	 * @param data
	 *            the data to be verified.
	 * @param sign
	 *            the data signature.
	 * @param publicKey
	 *            the public key.
	 *
	 * @return <code>true</code> if verification successes.
	 */
	public boolean verify(byte[] data, byte[] sign, PublicKey publicKey) {
		return signer.verify(data, sign, publicKey.getEncoded());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		if (signer != null) {
			signer.close();
		}
	}

}
