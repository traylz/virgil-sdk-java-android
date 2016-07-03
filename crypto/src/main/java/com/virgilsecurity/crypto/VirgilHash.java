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

public class VirgilHash extends VirgilAsn1Compatible implements java.lang.AutoCloseable {
	private long swigCPtr;

	protected VirgilHash(long cPtr, boolean cMemoryOwn) {
		super(virgil_crypto_javaJNI.VirgilHash_SWIGUpcast(cPtr), cMemoryOwn);
		swigCPtr = cPtr;
	}

	protected static long getCPtr(VirgilHash obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}

	protected void finalize() {
		delete();
	}

	public synchronized void delete() {
		if (swigCPtr != 0) {
			if (swigCMemOwn) {
				swigCMemOwn = false;
				virgil_crypto_javaJNI.delete_VirgilHash(swigCPtr);
			}
			swigCPtr = 0;
		}
		super.delete();
	}

	@Override
	public void close() {
		delete();
	}

	public static VirgilHash md5() {
		return new VirgilHash(virgil_crypto_javaJNI.VirgilHash_md5(), true);
	}

	public static VirgilHash sha256() {
		return new VirgilHash(virgil_crypto_javaJNI.VirgilHash_sha256(), true);
	}

	public static VirgilHash sha384() {
		return new VirgilHash(virgil_crypto_javaJNI.VirgilHash_sha384(), true);
	}

	public static VirgilHash sha512() {
		return new VirgilHash(virgil_crypto_javaJNI.VirgilHash_sha512(), true);
	}

	public static VirgilHash withName(byte[] name) {
		return new VirgilHash(virgil_crypto_javaJNI.VirgilHash_withName(name), true);
	}

	public VirgilHash() {
		this(virgil_crypto_javaJNI.new_VirgilHash__SWIG_0(), true);
	}

	public String name() {
		return virgil_crypto_javaJNI.VirgilHash_name(swigCPtr, this);
	}

	public int type() {
		return virgil_crypto_javaJNI.VirgilHash_type(swigCPtr, this);
	}

	public byte[] hash(byte[] bytes) {
		return virgil_crypto_javaJNI.VirgilHash_hash(swigCPtr, this, bytes);
	}

	public void start() {
		virgil_crypto_javaJNI.VirgilHash_start(swigCPtr, this);
	}

	public void update(byte[] bytes) {
		virgil_crypto_javaJNI.VirgilHash_update(swigCPtr, this, bytes);
	}

	public byte[] finish() {
		return virgil_crypto_javaJNI.VirgilHash_finish(swigCPtr, this);
	}

	public byte[] hmac(byte[] key, byte[] bytes) {
		return virgil_crypto_javaJNI.VirgilHash_hmac(swigCPtr, this, key, bytes);
	}

	public void hmacStart(byte[] key) {
		virgil_crypto_javaJNI.VirgilHash_hmacStart(swigCPtr, this, key);
	}

	public void hmacReset() {
		virgil_crypto_javaJNI.VirgilHash_hmacReset(swigCPtr, this);
	}

	public void hmacUpdate(byte[] bytes) {
		virgil_crypto_javaJNI.VirgilHash_hmacUpdate(swigCPtr, this, bytes);
	}

	public byte[] hmacFinish() {
		return virgil_crypto_javaJNI.VirgilHash_hmacFinish(swigCPtr, this);
	}

	public VirgilHash(VirgilHash other) {
		this(virgil_crypto_javaJNI.new_VirgilHash__SWIG_1(VirgilHash.getCPtr(other), other), true);
	}

}
