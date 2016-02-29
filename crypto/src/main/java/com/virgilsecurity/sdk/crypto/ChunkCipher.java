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

import com.virgilsecurity.crypto.VirgilChunkCipher;

/**
 * This class provides the functionality of a cryptographic cipher for
 * encryption and decryption by chunks.
 * 
 * @author Andrii Iakovenko
 */
public class ChunkCipher extends CipherBase {

	public final static int PREFERRED_CHUNK_SIZE = VirgilChunkCipher.kPreferredChunkSize;

	/**
	 * Create a new instance of {@code ChunkCipher}
	 *
	 */
	public ChunkCipher() {
		cipher = new VirgilChunkCipher();
	}

	/**
	 * Start encryption.
	 * 
	 * @param preferredChunkSize
	 *            the preferred chunk size.
	 * @return
	 */
	public long startEncryption(long preferredChunkSize) {
		return ((VirgilChunkCipher) cipher).startEncryption(preferredChunkSize);
	}

	/**
	 * Start encryption.
	 * 
	 * @return
	 */
	public long startEncryption() {
		return ((VirgilChunkCipher) cipher).startEncryption();
	}

	/**
	 * Start decryption with private key.
	 * 
	 * @param recipient
	 *            the key recipient.
	 * @param privateKey
	 *            the private key of recipient.
	 * @param privateKeyPassword
	 *            the password of private key.
	 * @return
	 */
	public long startDecryptionWithKey(Recipient recipient, PrivateKey privateKey, Password privateKeyPassword) {
		return ((VirgilChunkCipher) cipher).startDecryptionWithKey(recipient.getId(), privateKey.getEncoded(),
				privateKeyPassword.getEncoded());
	}

	/**
	 * Start decryption with private key.
	 * 
	 * @param recipient
	 *            the key recipient.
	 * @param privateKey
	 *            the private key of recipient.
	 * @return
	 */
	public long startDecryptionWithKey(Recipient recipient, PrivateKey privateKey) {
		return ((VirgilChunkCipher) cipher).startDecryptionWithKey(recipient.getId(), privateKey.getEncoded());
	}

	/**
	 * Start decryption with password.
	 * 
	 * @param password
	 *            the password.
	 * @return
	 */
	public long startDecryptionWithPassword(Password password) {
		return ((VirgilChunkCipher) cipher).startDecryptionWithPassword(password.getEncoded());
	}

	/**
	 * Process chunk.
	 * 
	 * @param data
	 *            the data to be processed.
	 * @return the result of chunk processing.
	 */
	public byte[] process(byte[] data) {
		return ((VirgilChunkCipher) cipher).process(data);
	}

	/**
	 * Finish data processing.
	 */
	public void finish() {
		((VirgilChunkCipher) cipher).finish();
	}

}
