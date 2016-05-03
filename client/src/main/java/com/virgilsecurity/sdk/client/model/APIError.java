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
package com.virgilsecurity.sdk.client.model;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

/**
 * The {@code APIError} class is the superclass for errors which can be occurred
 * on Virgil services call.
 * 
 * @author Andrii Iakovenko
 */
public class APIError {

	/** Request status code. */
	private int statusCode;

	/** Virgil error code. */
	@SerializedName("code")
	private int errorCode;

	/** Error message. */
	private String message;

	private Throwable throwable;

	/**
	 * Create a new instance of {@code APIError}.
	 *
	 * @param throwable
	 *            the cause of an error.
	 */
	public APIError(Throwable throwable) {
		this.throwable = throwable;
	}

	/**
	 * Create a new instance {@code APIError}.
	 *
	 * @param statusCode
	 *            the status code of response from Virgil service.
	 * @param errorBody
	 *            the body of response from Virgil service. Virgil service
	 *            provides detailed description of error as JSON.
	 */
	public APIError(int statusCode, String errorBody) {
		this.statusCode = statusCode;
		try {
			JsonElement bodyAsJson = new JsonParser().parse(errorBody);
			if (bodyAsJson.isJsonObject()) {
				errorCode = ((JsonObject) bodyAsJson).get("code").getAsInt();
			}
		} catch (JsonSyntaxException e) {
			// TODO Response error body parse error
		}
	}

	/**
	 * Returns the detail message string of this error.
	 * 
	 * @return the detail message string of this {@code APIError} instance
	 *         (which may be {@code null})
	 */
	public String getMessage() {
		if (message != null) {
			return message;
		}
		ResourceBundle bundle = null;
		try {
			bundle = ResourceBundle.getBundle("Messages");
			String key = getErrorPrefix() + errorCode;
			if (bundle.containsKey(key)) {
				return bundle.getString(key);
			} else if (bundle.containsKey("error_unknown")) {
				return bundle.getString("error_unknown");
			}
			return "";
		} catch (MissingResourceException e) {
			return "";
		}
	}

	/**
	 * Returns the prefix of error message which stored in
	 * {@code Messages.properties}.
	 * 
	 * @return the prefix of error message.
	 */
	protected String getErrorPrefix() {
		return "error";
	}

	/**
	 * Returns the code of an error which occurred on Virgil service.
	 * 
	 * @return the code of an error
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * Sets the code of an error which occurred on Virgil service.
	 * 
	 * @param errorCode
	 *            the code of an error
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Returns the status code of response from Virgil service.
	 * 
	 * @return the status code of response from Virgil service.
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Sets the status code of response from Virgil service.
	 * 
	 * @param statusCode
	 *            the status code of response from Virgil service.
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Returns the cause of an error.
	 * 
	 * @return the cause of an error (which may be {@code null}).
	 */
	public Throwable getThrowable() {
		return throwable;
	}

	/**
	 * Sets the cause of an error.
	 * 
	 * @param throwable
	 *            the cause of an error.
	 */
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("APIError { ");
		sb.append("statusCode = ").append(statusCode);
		sb.append(", ").append("errorCode = ").append(errorCode);
		sb.append(", ").append("message = ").append(getMessage());
		return sb.append(" }").toString();
	}

}
