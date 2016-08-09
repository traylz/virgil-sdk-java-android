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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.virgilsecurity.crypto.VirgilStreamCipher;
import com.virgilsecurity.crypto.VirgilStreamDataSink;
import com.virgilsecurity.crypto.VirgilStreamDataSource;

/**
 * This class provides the functionality of a cryptographic cipher for stream
 * encryption and decryption.
 *
 * @author Andrii Iakovenko
 *
 */
public class StreamCipher extends CipherBase implements java.lang.AutoCloseable {

	private static final Logger LOGGER = Logger.getLogger(StreamCipher.class.getName());

	/**
	 * Create a new instance of {@code StreamCipher}
	 *
	 */
	public StreamCipher() {
		cipher = new VirgilStreamCipher();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		cipher.close();
	}

	/**
	 * Encrypt stream data.
	 * 
	 * @param inputStream
	 *            the input stream to be encrypted.
	 * @param outputStream
	 *            encrypted data will be written to this stream.
	 * @param embedContentInfo
	 *            {@code true} if content information should be embedded into
	 *            data.
	 */
	public void encrypt(InputStream inputStream, OutputStream outputStream, boolean embedContentInfo) {
		try (VirgilStreamDataSource dataSource = new VirgilStreamDataSource(inputStream);
				VirgilStreamDataSink dataSink = new VirgilStreamDataSink(outputStream)) {
			((VirgilStreamCipher) cipher).encrypt(dataSource, dataSink, embedContentInfo);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Can't close resource", e);
		}
	}

	/**
	 * Encrypt stream data.
	 * 
	 * @param inputStream
	 *            the input stream to be encrypted.
	 * @param outputStream
	 *            encrypted data will be written to this stream.
	 */
	public void encrypt(InputStream inputStream, OutputStream outputStream) {
		try (VirgilStreamDataSource dataSource = new VirgilStreamDataSource(inputStream);
				VirgilStreamDataSink dataSink = new VirgilStreamDataSink(outputStream)) {
			((VirgilStreamCipher) cipher).encrypt(dataSource, dataSink);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Can't close resource", e);
		}
	}

	/**
	 * Decrypt encrypted data from stream with key protected by password.
	 * 
	 * @param inputStream
	 *            the input stream to be decrypted.
	 * @param outputStream
	 *            decrypted data will be written to this stream.
	 * @param recipient
	 *            the recipient.
	 * @param privateKey
	 *            the private key used for data decryption.
	 * @param privateKeyPassword
	 *            the private key password.
	 */
	public void decryptWithKey(InputStream inputStream, OutputStream outputStream, Recipient recipient,
			PrivateKey privateKey, Password privateKeyPassword) {
		try (VirgilStreamDataSource dataSource = new VirgilStreamDataSource(inputStream);
				VirgilStreamDataSink dataSink = new VirgilStreamDataSink(outputStream)) {
			((VirgilStreamCipher) cipher).decryptWithKey(dataSource, dataSink, recipient.getId(), privateKey.getEncoded(),
					privateKeyPassword.getEncoded());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Can't close resource", e);
		}
	}

	/**
	 * Decrypt encrypted data from stream with key protected by password.
	 * 
	 * @param inputStream
	 *            the input stream to be decrypted.
	 * @param outputStream
	 *            decrypted data will be written to this stream.
	 * @param recipient
	 *            the recipient.
	 * @param privateKey
	 *            the private key used for data decryption.
	 */
	public void decryptWithKey(InputStream inputStream, OutputStream outputStream, Recipient recipient,
			PrivateKey privateKey) {
		try (VirgilStreamDataSource dataSource = new VirgilStreamDataSource(inputStream);
				VirgilStreamDataSink dataSink = new VirgilStreamDataSink(outputStream)) {
			((VirgilStreamCipher) cipher).decryptWithKey(dataSource, dataSink, recipient.getId(), privateKey.getEncoded());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Can't close resource", e);
		}
	}

	/**
	 * Decrypt encrypted data from stream with password.
	 * 
	 * @param inputStream
	 *            the input stream to be decrypted.
	 * @param outputStream
	 *            decrypted data will be written to this stream.
	 * @param password
	 *            the password used for data decryption.
	 */
	public void decryptWithPassword(InputStream inputStream, OutputStream outputStream, Password password) {
		try (VirgilStreamDataSource dataSource = new VirgilStreamDataSource(inputStream);
				VirgilStreamDataSink dataSink = new VirgilStreamDataSink(outputStream)) {
			((VirgilStreamCipher) cipher).decryptWithPassword(dataSource, dataSink, password.getEncoded());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Can't close resource", e);
		}
	}

}
