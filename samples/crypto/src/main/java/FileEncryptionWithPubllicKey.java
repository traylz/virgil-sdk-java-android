import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;
import com.virgilsecurity.sdk.crypto.Recipient;
import com.virgilsecurity.sdk.crypto.StreamCipher;

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

/**
 * This example shows how to encrypt file with public key and decrypt it back.
 * 
 * @author Andrii Iakovenko
 *
 */
public class FileEncryptionWithPubllicKey {

	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter path to file: ");
		String fileName = br.readLine();
		System.out.println();

		KeyPair keyPair = KeyPairGenerator.generate();
		Recipient recipient = new Recipient(UUID.randomUUID().toString());

		// Encode file
		String encodedFileName = fileName + ".encoded";
		System.out.println("Encoding file");
		try (StreamCipher cipher = new StreamCipher();
				InputStream in = new FileInputStream(fileName);
				OutputStream out = new FileOutputStream(encodedFileName)) {

			cipher.addKeyRecipient(recipient, keyPair.getPublic());
			cipher.encrypt(in, out, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Decode file
		String decodedFileName = fileName + ".decoded";
		System.out.println("Decoding file");
		try (StreamCipher cipher = new StreamCipher();
				InputStream in = new FileInputStream(encodedFileName);
				OutputStream out = new FileOutputStream(decodedFileName)) {
			
			cipher.decryptWithKey(in, out, recipient, keyPair.getPrivate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Done");
	}
}
