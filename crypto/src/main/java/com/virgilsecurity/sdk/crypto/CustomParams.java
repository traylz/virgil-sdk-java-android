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

import com.virgilsecurity.crypto.VirgilAsn1Compatible;
import com.virgilsecurity.crypto.VirgilCustomParams;

/**
 * Custom parameters could be added to ciphered data.
 * 
 * @author Andrii Iakovenko
 */
public class CustomParams extends Asn1Compatible {

	private final VirgilCustomParams wrappedParams;

	/**
	 * Create new instance of {@code CustomParams}.
	 */
	public CustomParams() {
		this.wrappedParams = new VirgilCustomParams();
	}

	/**
	 * Create new instance of {@code CustomParams}.
	 *
	 * @param params
	 *            the parameters for created new {@code CustomParams}
	 */
	public CustomParams(CustomParams params) {
		this.wrappedParams = new VirgilCustomParams(params.wrappedParams);
	}

	/**
	 * Create new instance of CustomParams.
	 *
	 * @param params
	 *            VirgilCustomParams to be wrapped.
	 */
	CustomParams(VirgilCustomParams params) {
		this.wrappedParams = params;
	}

	/**
	 * @return {@code true} if custom parameters are empty.
	 */
	public boolean isEmpty() {
		return wrappedParams.isEmpty();
	}

	/**
	 * Clear custom parameters.
	 */
	public void clear() {
		wrappedParams.clear();
	}

	/**
	 * Set custom parameter of {@code Integer} type.
	 * 
	 * @param key
	 *            the parameter's key (name).
	 * @param value
	 *            the parameter's value.
	 */
	public void setInteger(Key key, int value) {
		wrappedParams.setInteger(key.getEncoded(), value);
	}

	/**
	 * Get value of custom parameter of {@code Integer} type by key.
	 * 
	 * @param key
	 *            the parameter's key (name).
	 * @return
	 */
	public int getInteger(Key key) {
		return wrappedParams.getInteger(key.getEncoded());
	}

	/**
	 * Remove parameter of {@code Integer} type by key.
	 * 
	 * @param key
	 *            the parameter's key (name).
	 */
	public void removeInteger(Key key) {
		wrappedParams.removeInteger(key.getEncoded());
	}

	/**
	 * Set custom parameter of {@code String} type.
	 *
	 * 
	 * @param key
	 *            the parameter's key (name).
	 * @param value
	 *            the parameter's value.
	 */
	public void setString(Key key, byte[] value) {
		wrappedParams.setString(key.getEncoded(), value);
	}

	/**
	 * Get value of custom parameter of {@code String} type by key.
	 * 
	 * @param key
	 *            the parameter's key (name).
	 * @return
	 */
	public byte[] getString(Key key) {
		return wrappedParams.getString(key.getEncoded());
	}

	/**
	 * Remove parameter of {@code String} type by key.
	 * 
	 * @param key
	 *            the parameter's key (name).
	 */
	public void removeString(Key key) {
		wrappedParams.removeString(key.getEncoded());
	}

	/**
	 * Set binary custom parameter.
	 *
	 * 
	 * @param key
	 *            the parameter's key (name).
	 * @param value
	 *            the parameter's value.
	 */
	public void setData(Key key, byte[] value) {
		wrappedParams.setData(key.getEncoded(), value);
	}

	/**
	 * Get binary parameter by key
	 * 
	 * @param key
	 *            the parameter's key (name).
	 * @return
	 */
	public byte[] getData(Key key) {
		return wrappedParams.getData(key.getEncoded());
	}

	/**
	 * Remove binary parameter by key.
	 * 
	 * @param key
	 *            the parameter's key (name).
	 */
	public void removeData(Key key) {
		wrappedParams.removeData(key.getEncoded());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.virgilsecurity.sdk.crypto.Asn1Compatible#getWrappedASN()
	 */
	@Override
	protected VirgilAsn1Compatible getWrappedASN() {
		return wrappedParams;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.virgilsecurity.sdk.crypto.Asn1Compatible#close()
	 */
	@Override
	public void close() throws Exception {
		wrappedParams.close();
	}

}
