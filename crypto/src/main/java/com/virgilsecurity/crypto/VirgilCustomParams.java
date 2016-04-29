/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.virgilsecurity.crypto;

public class VirgilCustomParams extends VirgilAsn1Compatible implements java.lang.AutoCloseable {
	private long swigCPtr;

	protected VirgilCustomParams(long cPtr, boolean cMemoryOwn) {
		super(virgil_crypto_javaJNI.VirgilCustomParams_SWIGUpcast(cPtr), cMemoryOwn);
		swigCPtr = cPtr;
	}

	protected static long getCPtr(VirgilCustomParams obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}

	protected void finalize() {
		delete();
	}

	public synchronized void delete() {
		if (swigCPtr != 0) {
			if (swigCMemOwn) {
				swigCMemOwn = false;
				virgil_crypto_javaJNI.delete_VirgilCustomParams(swigCPtr);
			}
			swigCPtr = 0;
		}
		super.delete();
	}

	@Override
	public void close() {
		delete();
	}

	public boolean isEmpty() {
		return virgil_crypto_javaJNI.VirgilCustomParams_isEmpty(swigCPtr, this);
	}

	public void setInteger(byte[] key, int value) {
		virgil_crypto_javaJNI.VirgilCustomParams_setInteger(swigCPtr, this, key, value);
	}

	public int getInteger(byte[] key) {
		return virgil_crypto_javaJNI.VirgilCustomParams_getInteger(swigCPtr, this, key);
	}

	public void removeInteger(byte[] key) {
		virgil_crypto_javaJNI.VirgilCustomParams_removeInteger(swigCPtr, this, key);
	}

	public void setString(byte[] key, byte[] value) {
		virgil_crypto_javaJNI.VirgilCustomParams_setString(swigCPtr, this, key, value);
	}

	public byte[] getString(byte[] key) {
		return virgil_crypto_javaJNI.VirgilCustomParams_getString(swigCPtr, this, key);
	}

	public void removeString(byte[] key) {
		virgil_crypto_javaJNI.VirgilCustomParams_removeString(swigCPtr, this, key);
	}

	public void setData(byte[] key, byte[] value) {
		virgil_crypto_javaJNI.VirgilCustomParams_setData(swigCPtr, this, key, value);
	}

	public byte[] getData(byte[] key) {
		return virgil_crypto_javaJNI.VirgilCustomParams_getData(swigCPtr, this, key);
	}

	public void removeData(byte[] key) {
		virgil_crypto_javaJNI.VirgilCustomParams_removeData(swigCPtr, this, key);
	}

	public void clear() {
		virgil_crypto_javaJNI.VirgilCustomParams_clear(swigCPtr, this);
	}

	public VirgilCustomParams() {
		this(virgil_crypto_javaJNI.new_VirgilCustomParams__SWIG_0(), true);
	}

	public VirgilCustomParams(VirgilCustomParams other) {
		this(virgil_crypto_javaJNI.new_VirgilCustomParams__SWIG_1(VirgilCustomParams.getCPtr(other), other), true);
	}

}
