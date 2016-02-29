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

/**
 * This enum contains key types supported by Virgil Crypto.
 *
 * @author Andrii Iakovenko
 *
 */
public enum KeyType {

	/** Recommended most safe type */
	Default,
	/** RSA 1024 bit (not recommended) */
	RSA_256,
	/** RSA 1024 bit (not recommended) */
	RSA_512,
	/** RSA 1024 bit (not recommended) */
	RSA_1024,
	/** RSA 2048 bit (not recommended) */
	RSA_2048,
	/** RSA 3072 bit */
	// RSA_3072,
	/** RSA 4096 bit */
	RSA_4096,
	/** RSA 8192 bit */
	// RSA_8192,
	/** 192-bits NIST curve */
	EC_SECP192R1,
	/** 224-bits NIST curve */
	EC_SECP224R1,
	/** 256-bits NIST curve */
	EC_SECP256R1,
	/** 384-bits NIST curve */
	EC_SECP384R1,
	/** 521-bits NIST curve */
	EC_SECP521R1,
	/** 256-bits Brainpool curve */
	EC_BP256R1,
	/** 384-bits Brainpool curve */
	EC_BP384R1,
	/** 512-bits Brainpool curve */
	EC_BP512R1,
	/** (not implemented yet) */
	// EC_M221,
	/** Curve25519 */
	// EC_M255,
	/** (not implemented yet) */
	// EC_M383,
	/** (not implemented yet) */
	// EC_M511,
	/** 192-bits "Koblitz" curve */
	EC_SECP192K1,
	/** 224-bits "Koblitz" curve */
	EC_SECP224K1,
	/** 256-bits "Koblitz" curve */
	EC_SECP256K1

}
