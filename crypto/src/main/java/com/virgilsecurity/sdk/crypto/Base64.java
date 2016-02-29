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
package com.virgilsecurity.sdk.crypto;

import com.virgilsecurity.crypto.virgil_crypto_javaJNI;

/**
 * This class consists exclusively of static methods for obtaining encoders and
 * decoders for the Base64 encoding scheme. The implementation of this class
 * supports the following types of {@code Base64} as specified in
 * {@code RFC 4648} and {@code RFC 2045}.
 *
 * @author Andrii Iakovenko
 */
public class Base64 {

	/**
	 * Encodes all bytes from the specified byte array into a newly-allocated
	 * byte array using the {@code Base64} encoding scheme.
	 * 
	 * @param src
	 *            the byte array to encode
	 * @return
	 */
	public static String encode(byte[] src) {
		return virgil_crypto_javaJNI.VirgilBase64_encode(src);
	}

	/**
	 * Decodes all bytes from the input byte array using the {@code Base64}
	 * encoding scheme, writing the results into a newly-allocated output byte
	 * array.
	 * 
	 * @param src
	 *            the byte array to decode
	 * 
	 * @return
	 */
	public static byte[] decode(String src) {
		return virgil_crypto_javaJNI.VirgilBase64_decode(src);
	}

}
