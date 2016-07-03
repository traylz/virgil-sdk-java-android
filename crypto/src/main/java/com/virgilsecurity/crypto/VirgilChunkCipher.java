/**
 * Copyright (C) 2016 Virgil Security Inc.
 *
 * Lead Maintainer: Virgil Security Inc. <support@virgilsecurity.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     (2) Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 *     (3) Neither the name of the copyright holder nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.virgilsecurity.crypto;

public class VirgilChunkCipher extends VirgilCipherBase implements java.lang.AutoCloseable {
	private long swigCPtr;

	protected VirgilChunkCipher(long cPtr, boolean cMemoryOwn) {
		super(virgil_crypto_javaJNI.VirgilChunkCipher_SWIGUpcast(cPtr), cMemoryOwn);
		swigCPtr = cPtr;
	}

	protected static long getCPtr(VirgilChunkCipher obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}

	protected void finalize() {
		delete();
	}

	public synchronized void delete() {
		if (swigCPtr != 0) {
			if (swigCMemOwn) {
				swigCMemOwn = false;
				virgil_crypto_javaJNI.delete_VirgilChunkCipher(swigCPtr);
			}
			swigCPtr = 0;
		}
		super.delete();
	}

	@Override
	public void close() {
		delete();
	}

	public long startEncryption(long preferredChunkSize) {
		return virgil_crypto_javaJNI.VirgilChunkCipher_startEncryption__SWIG_0(swigCPtr, this, preferredChunkSize);
	}

	public long startEncryption() {
		return virgil_crypto_javaJNI.VirgilChunkCipher_startEncryption__SWIG_1(swigCPtr, this);
	}

	public long startDecryptionWithKey(byte[] recipientId, byte[] privateKey, byte[] privateKeyPassword) {
		return virgil_crypto_javaJNI.VirgilChunkCipher_startDecryptionWithKey__SWIG_0(swigCPtr, this, recipientId,
				privateKey, privateKeyPassword);
	}

	public long startDecryptionWithKey(byte[] recipientId, byte[] privateKey) {
		return virgil_crypto_javaJNI.VirgilChunkCipher_startDecryptionWithKey__SWIG_1(swigCPtr, this, recipientId,
				privateKey);
	}

	public long startDecryptionWithPassword(byte[] pwd) {
		return virgil_crypto_javaJNI.VirgilChunkCipher_startDecryptionWithPassword(swigCPtr, this, pwd);
	}

	public byte[] process(byte[] data) {
		return virgil_crypto_javaJNI.VirgilChunkCipher_process(swigCPtr, this, data);
	}

	public void finish() {
		virgil_crypto_javaJNI.VirgilChunkCipher_finish(swigCPtr, this);
	}

	public VirgilChunkCipher() {
		this(virgil_crypto_javaJNI.new_VirgilChunkCipher(), true);
	}

	public final static int kPreferredChunkSize = virgil_crypto_javaJNI.VirgilChunkCipher_kPreferredChunkSize_get();

}
