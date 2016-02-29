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
import com.virgilsecurity.crypto.VirgilKeyPair.Type;

/**
 * This class generates key pairs with algorithms supported by Virgil Crypto.
 * 
 * @author Andrii Iakovenko
 */
public class KeyPairGenerator {

	/**
	 * Generate key pair with default Key Type
	 * 
	 * @return generated KeyPair
	 * @see KeyPair
	 */
	public static KeyPair generate() {
		return generate(KeyType.Default);
	}

	/**
	 * Generate key pair with default Key Type
	 * 
	 * @param password
	 *            the password used for Private Key encryption.
	 * @return generated KeyPair
	 * @see KeyPair
	 */
	public static KeyPair generate(String password) {
		return generate(KeyType.Default, password);
	}

	/**
	 * Generate key pair by Type
	 * 
	 * @param keyType
	 *            the key's type.
	 * @return generated key pair.
	 * 
	 * @see KeyPair
	 */
	public static KeyPair generate(KeyType keyType) {
		VirgilKeyPair pair = null;
		switch (keyType) {
		case Default:
			pair = VirgilKeyPair.generate();
			break;
		case EC_BP256R1:
			pair = VirgilKeyPair.ecBrainpool256();
			break;
		case EC_BP384R1:
			pair = VirgilKeyPair.ecBrainpool384();
			break;
		case EC_BP512R1:
			pair = VirgilKeyPair.ecBrainpool512();
			break;
		case EC_SECP192K1:
			pair = VirgilKeyPair.ecKoblitz192();
			break;
		case EC_SECP224K1:
			pair = VirgilKeyPair.ecKoblitz224();
			break;
		case EC_SECP256K1:
			pair = VirgilKeyPair.ecKoblitz256();
			break;
		case EC_SECP192R1:
			pair = VirgilKeyPair.ecNist192();
			break;
		case EC_SECP224R1:
			pair = VirgilKeyPair.ecNist224();
			break;
		case EC_SECP256R1:
			pair = VirgilKeyPair.ecNist256();
			break;
		case EC_SECP384R1:
			pair = VirgilKeyPair.ecNist384();
			break;
		case EC_SECP521R1:
			pair = VirgilKeyPair.ecNist521();
			break;
		case RSA_256:
			pair = VirgilKeyPair.rsa256();
			break;
		case RSA_512:
			pair = VirgilKeyPair.rsa512();
			break;
		case RSA_1024:
			pair = VirgilKeyPair.rsa1024();
			break;
		case RSA_2048:
			pair = VirgilKeyPair.rsa2048();
			break;
		case RSA_4096:
			pair = VirgilKeyPair.rsa4096();
			break;
		default:
			throw new IllegalArgumentException("Key type " + keyType + " is not supported");
		}

		return new KeyPair(pair);
	}

	/**
	 * Generate key pair by Type.
	 * 
	 * @param keyType
	 *            the key's type.
	 * @param password
	 *            the password for private key protection.
	 * 
	 * @return generated key pair.
	 * 
	 * @see PrivateKey
	 */
	public static KeyPair generate(KeyType keyType, Password password) {
		VirgilKeyPair pair = null;
		switch (keyType) {
		case Default:
			pair = VirgilKeyPair.generate(Type.Default, password.getEncoded());
			break;
		case EC_BP256R1:
			pair = VirgilKeyPair.ecBrainpool256(password.getEncoded());
			break;
		case EC_BP384R1:
			pair = VirgilKeyPair.ecBrainpool384(password.getEncoded());
			break;
		case EC_BP512R1:
			pair = VirgilKeyPair.ecBrainpool512(password.getEncoded());
			break;
		case EC_SECP192K1:
			pair = VirgilKeyPair.ecKoblitz192(password.getEncoded());
			break;
		case EC_SECP224K1:
			pair = VirgilKeyPair.ecKoblitz224(password.getEncoded());
			break;
		case EC_SECP256K1:
			pair = VirgilKeyPair.ecKoblitz256(password.getEncoded());
			break;
		case EC_SECP192R1:
			pair = VirgilKeyPair.ecNist192(password.getEncoded());
			break;
		case EC_SECP224R1:
			pair = VirgilKeyPair.ecNist224(password.getEncoded());
			break;
		case EC_SECP256R1:
			pair = VirgilKeyPair.ecNist256(password.getEncoded());
			break;
		case EC_SECP384R1:
			pair = VirgilKeyPair.ecNist384(password.getEncoded());
			break;
		case EC_SECP521R1:
			pair = VirgilKeyPair.ecNist521(password.getEncoded());
			break;
		case RSA_256:
			pair = VirgilKeyPair.rsa256(password.getEncoded());
			break;
		case RSA_512:
			pair = VirgilKeyPair.rsa512(password.getEncoded());
			break;
		case RSA_1024:
			pair = VirgilKeyPair.rsa1024(password.getEncoded());
			break;
		case RSA_2048:
			pair = VirgilKeyPair.rsa2048(password.getEncoded());
			break;
		case RSA_4096:
			pair = VirgilKeyPair.rsa4096(password.getEncoded());
			break;
		default:
			throw new IllegalArgumentException("Key type " + keyType + " is not supported");
		}

		return new KeyPair(pair);
	}

	/**
	 * Generate key pair by Type.
	 * 
	 * @param keyType
	 *            the key's type.
	 * @param password
	 *            the password for private key protection.
	 * 
	 * @return generated key pair.
	 * 
	 * @see PrivateKey
	 */
	public static KeyPair generate(KeyType keyType, String password) {
		return generate(keyType, new Password(password));
	}

}
