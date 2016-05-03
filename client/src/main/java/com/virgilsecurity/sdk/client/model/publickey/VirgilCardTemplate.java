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
package com.virgilsecurity.sdk.client.model.publickey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.virgilsecurity.sdk.client.model.Identity;
import com.virgilsecurity.sdk.client.utils.StringUtils;
import com.virgilsecurity.sdk.crypto.PublicKey;

/**
 * This class used for registering new Virgil Card.
 * 
 * <ul>
 * <li>it's mandatory to specify either {@code publicKeyId} or {@code publicKey}
 * parameter
 * <li>to create a confirmed Identity it's necessary to pass {@code token}
 * parameter obtained from the Virgil Identity service
 * <li>if created Virgil Card is unconfirmed it will not appear in the search
 * results by default
 * </ul>
 * 
 * @author Andrii Iakovenko
 */
public class VirgilCardTemplate {

	@SerializedName("public_key_id")
	private String publicKeyId;

	@SerializedName("public_key")
	private String publicKey;

	@SerializedName("identity")
	private Identity identity;

	@SerializedName("data")
	private Map<String, String> data;

	@SerializedName("signs")
	private List<Sign> signs;

	/**
	 * Returns the public key's identifier.
	 * 
	 * In order to attach the Virgil Card to the existing Public Key you should
	 * pass {@code publicKeyId} that holds the Public Key's ID.
	 * 
	 * @return the public key's identifier.
	 */
	public String getPublicKeyId() {
		return publicKeyId;
	}

	/**
	 * Sets the public key's identifier.
	 * 
	 * @param publicKeyId
	 *            the public key's identifier
	 */
	public void setPublicKeyId(String publicKeyId) {
		this.publicKeyId = publicKeyId;
	}

	/**
	 * Returns the public key.
	 * 
	 * In order to create new Public Key instance you should pass
	 * {@code publicKey}
	 * 
	 * @return the public key as {@code Base64 string}.
	 */
	public String getPublicKey() {
		return publicKey;
	}

	/**
	 * Sets the public key.
	 * 
	 * @param publicKey
	 *            the public key as {@code Base64 string}.
	 */
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	/**
	 * Returns the Virgil identity associated with created card.
	 * 
	 * The {@code identity} stands for the the Virgil Card identity that can
	 * have a type of 'email'
	 * 
	 * @return the Virgil identity.
	 */
	public Identity getIdentity() {
		return identity;
	}

	/**
	 * Sets the Virgil identity associated with created card.
	 * 
	 * @param identity
	 *            the Virgil identity.
	 */
	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	/**
	 * Returns Virgil Card's data as key/value map.
	 * 
	 * @return the card data.
	 */
	public Map<String, String> getData() {
		return data;
	}

	/**
	 * @param data
	 *            the card data.
	 */
	public void setData(Map<String, String> data) {
		this.data = data;
	}

	/**
	 * To automatically create a list of signs for created Virgil Card it's
	 * possible to pass the list of signs as {@code signs}.
	 * 
	 * @return the Virgil Card signs.
	 */
	public List<Sign> getSigns() {
		return signs;
	}

	/**
	 * @param signs
	 *            the Virgil Card signs.
	 */
	public void setSigns(List<Sign> signs) {
		this.signs = signs;
	}

	/**
	 * Use this builder to construct {@code VirgilCardTemplate}.
	 *
	 * @author Andrii Iakovenko
	 *
	 */
	public static class Builder {
		private String publicKeyId;
		private String publicKey;
		private Identity identity;
		private Map<String, String> data;
		private List<Sign> signs;

		/**
		 * Set public key's identifier.
		 * 
		 * @param publicKeyId
		 *            the public key's identifier.
		 * @return the {@code Builder}.
		 */
		public Builder setPublicKeyId(String publicKeyId) {
			if (publicKey != null) {
				throw new IllegalArgumentException("Public Key is already set");
			} else if (StringUtils.isBlank(publicKeyId)) {
				throw new IllegalArgumentException("Public Key ID can't be blank");
			}

			this.publicKeyId = publicKeyId;

			return this;
		}

		/**
		 * Set public key.
		 * 
		 * @param publicKey
		 *            the public key.
		 * @return the {@code Builder}.
		 */
		public Builder setPublicKey(PublicKey publicKey) {
			this.publicKey = publicKey.getAsBase64String();
			return this;
		}

		/**
		 * Set public key.
		 * 
		 * @param publicKey
		 *            public key as {@code Base64} string.
		 * @return the {@code Builder}.
		 */
		public Builder setPublicKey(String publicKey) {
			if (publicKeyId != null) {
				throw new IllegalArgumentException("Public Key ID is already set");
			} else if (StringUtils.isBlank(publicKey)) {
				throw new IllegalArgumentException("Public Key can't be blank");
			}

			this.publicKey = publicKey;

			return this;
		}

		/**
		 * Set identity associated with card.
		 * 
		 * @param identity
		 *            the identity associated with card.
		 * @return the {@code Builder}.
		 */
		public Builder setIdentity(Identity identity) {
			this.identity = identity;

			return this;
		}

		/**
		 * Add data to card.
		 * 
		 * @param key
		 *            the data key.
		 * @param value
		 *            the data value.
		 * @return the {@code Builder}.
		 */
		public Builder addData(String key, String value) {
			if (data == null) {
				data = new HashMap<>();
			}
			data.put(key, value);

			return this;
		}

		/**
		 * Add sign to card.
		 * 
		 * @param sign
		 *            the sign.
		 * @return the {@code Builder}.
		 */
		public Builder addSign(Sign sign) {
			if (this.signs == null) {
				signs = new ArrayList<>();
			}
			signs.add(sign);

			return this;
		}

		/**
		 * @return the {@code VirgilCardTemplate} build.
		 */
		public VirgilCardTemplate build() {
			VirgilCardTemplate request = new VirgilCardTemplate();
			request.setPublicKeyId(this.publicKeyId);
			request.setPublicKey(this.publicKey);
			request.setIdentity(this.identity);
			request.setData(this.data);
			request.setSigns(this.signs);

			return request;
		}

	}

}
