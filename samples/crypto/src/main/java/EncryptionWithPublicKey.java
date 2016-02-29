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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;

import com.virgilsecurity.sdk.crypto.CryptoHelper;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;

/**
 * This example shows how to encrypt text with public key and decrypt it back
 * with private key.
 * 
 * @author Andrii Iakovenko
 *
 */
public class EncryptionWithPublicKey {

	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter text to encrypt: ");
		String text = br.readLine();
		System.out.println();

		// Generate generate public/private key pair for key recipient
		String recipientId = UUID.randomUUID().toString();

		KeyPair keyPair = KeyPairGenerator.generate();

		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		System.out.println("Generated keys for <Key Recepient>");
		System.out.println(String.format("Public Key: \n%1$s", new String(publicKey.getEncoded())));
		System.out.println(String.format("Private Key: \n%1$s", new String(privateKey.getEncoded())));

		// Encrypt data with public key
		String encryptedText = CryptoHelper.encrypt(text, recipientId, publicKey);

		System.out.println(String.format("Cipher Text in Base64: %1$s", encryptedText));

		// Decrypt data with private key
		String decryptedText = CryptoHelper.decrypt(encryptedText, recipientId, privateKey);
		System.out.println(String.format("Decrypted Text: %1$s", decryptedText));

	}
}
