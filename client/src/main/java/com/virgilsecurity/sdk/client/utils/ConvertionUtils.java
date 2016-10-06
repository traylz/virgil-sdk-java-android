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
package com.virgilsecurity.sdk.client.utils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.virgilsecurity.crypto.VirgilBase64;

/**
 * TODO: add type description
 *
 * @author Andrii Iakovenko
 *
 */
public class ConvertionUtils {

	private static Gson gson = null;

	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	public static synchronized Gson getGson() {
		if (gson == null) {
			GsonBuilder builder = new GsonBuilder();
			gson = builder.disableHtmlEscaping().create();
		}
		return gson;
	}

	/**
	 * Convert {@code String} to byte array.
	 * 
	 * @param string
	 * @return
	 */
	public static byte[] toBytes(String string) {
		if (string == null) {
			return new byte[0];
		}
		return string.getBytes(UTF8_CHARSET);
	}

	/**
	 * Convert byte array to {@code String}.
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toString(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return "";
		}
		return new String(bytes);
	}

	/**
	 * Encode string to Base64 string.
	 * 
	 * @param value
	 *            The string to be converted.
	 * @return
	 */
	public static String toBase64String(String value) {
		byte[] bytes = value.getBytes(UTF8_CHARSET);
		return VirgilBase64.encode(bytes);
	}

	public static byte[] toBase64Array(String value) {
		String str = VirgilBase64.encode(value.getBytes(UTF8_CHARSET));
		return toBytes(str);
	}

	/**
	 * Encode byte array as Base64 string.
	 * 
	 * @param bytes
	 *            the byte array to be encoded.
	 * @return
	 */
	public static String toBase64String(byte[] bytes) {
		return VirgilBase64.encode(bytes);
	}

	/**
	 * Decode Base64 string to string.
	 * 
	 * @param value
	 *            The string to be converted.
	 * @return
	 */
	public static String base64ToString(String value) {
		return toString(VirgilBase64.decode(value));
	}

	/**
	 * Decode Base64 string to byte array.
	 * 
	 * @param value
	 *            The string to be converted.
	 * @return
	 */
	public static byte[] base64ToArray(String value) {
		return VirgilBase64.decode(value);
	}

	public static String base64ToString(byte[] bytes) {
		return toString(VirgilBase64.decode(toString(bytes)));
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a String using UTF-8
	 * character encoding.
	 * 
	 * @param is
	 * @return
	 */
	public static String toString(InputStream is) {
		try (Scanner s = new Scanner(is, "UTF-8")) {
			s.useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
	}

}
