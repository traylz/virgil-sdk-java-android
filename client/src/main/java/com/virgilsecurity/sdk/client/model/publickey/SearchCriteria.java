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
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.virgilsecurity.sdk.client.utils.StringUtils;

/**
 * This class represents a search criteria:
 * 
 * <ul>
 * <li>the {@code value} request parameter is mandatory
 * <li>the {@code type} request parameter is optional and specifies the type of
 * Virgil Card's Identity
 * <li>the {@code relations} parameter is optional and contains the list of
 * Virgil Cards UDIDs to perform the search within
 * <li>the {@code includeUnauthorized} parameter specifies whether an
 * unauthorized Virgil Cards should be returned.
 * </ul>
 *
 * @author Andrii Iakovenko
 *
 */
public class SearchCriteria {

	/** The value of Virgil Card's Identity */
	@SerializedName("value")
	private String value;

	/** The type of Virgil Card's Identity */
	@SerializedName("type")
	private String type;

	/** Specifies whether an unauthorized Virgil Cards should be returned */
	@SerializedName("include_unauthorized")
	private Boolean includeUnauthorized;

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the includeUnauthorized
	 */
	public Boolean getIncludeUnauthorized() {
		return includeUnauthorized;
	}

	/**
	 * @param includeUnauthorized
	 *            the includeUnauthorized to set
	 */
	public void setIncludeUnauthorized(Boolean includeUnauthorized) {
		this.includeUnauthorized = includeUnauthorized;
	}

	/**
	 * An instance of this class encapsulates the information needed to
	 * instantiate and initialize a search criteria object.
	 *
	 * @see SearchCriteria
	 */
	public static class Builder {

		private String type;

		private String value;

		private List<String> relations;

		private Boolean includeUnauthorized;

		/**
		 * Create a new instance of Builder.
		 *
		 */
		public Builder() {
			this.relations = new ArrayList<>();
		}

		/**
		 * Set the identity type. The {@code email} type used by default.
		 * 
		 * @param type
		 *            the identity type.
		 * @return the {@code Builder}
		 */
		public Builder setType(String type) {
			this.type = type;
			return this;
		}

		/**
		 * Set the identity value.
		 * 
		 * @param value
		 *            the identity value.
		 * @return the {@code Builder}
		 */
		public Builder setValue(String value) {
			this.value = value;
			return this;
		}

		/**
		 * Add relation.
		 * 
		 * @param relation
		 *            the relation.
		 * @return the {@code Builder}
		 */
		public Builder addRelation(String relation) {
			this.relations.add(relation);
			return this;
		}

		/**
		 * This flag allows to include unconfirmed Virgil Cards into search
		 * result.
		 * 
		 * @param includeUnauthorized
		 *            the flag for unauthorized cards.
		 * @return the {@code Builder}
		 */
		public Builder setIncludeUnauthorized(Boolean includeUnauthorized) {
			this.includeUnauthorized = includeUnauthorized;

			return this;
		}

		/**
		 * Build search criteria.
		 * 
		 * @return the {@code SearchCriteria} built.
		 */
		public SearchCriteria build() {
			if (StringUtils.isBlank(this.value)) {
				throw new IllegalArgumentException("Value is not set");
			}
			SearchCriteria request = new SearchCriteria();
			request.setType(this.type);
			request.setValue(this.value);

			if (includeUnauthorized != null) {
				request.setIncludeUnauthorized(this.includeUnauthorized);
			}

			return request;

		}

	}

}
