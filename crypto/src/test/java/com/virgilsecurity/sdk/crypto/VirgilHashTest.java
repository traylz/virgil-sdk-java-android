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

import com.virgilsecurity.crypto.VirgilHash;
import javax.xml.bind.DatatypeConverter;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Andrii Iakovenko
 */
public class VirgilHashTest {
    
    private static final String TEST_STRING = "This is a test string for hash calculation";
    
    @Test
    public void testSha256String() {
        VirgilHash virgilHash = VirgilHash.sha256();
        byte[] hash = virgilHash.hash(TEST_STRING.getBytes());
        assertEquals("9494c4ceb6f0c1de890d5dc865f8f5ba3baf1b16b3c0b5fed585e5b1fcd1fa33", hashToString(hash));
    }
    
    @Test
    public void testSha384String() {
        VirgilHash virgilHash = VirgilHash.sha384();
        byte[] hash = virgilHash.hash(TEST_STRING.getBytes());
        assertEquals("bf606eb5f7aaa1412720de6d69e008443c3e26e21141d0a16b685be729f876dd4ff98da4e3153b3293844c5264c9a0e1", hashToString(hash));
    }
    
    @Test
    public void testSha512String() {
        VirgilHash virgilHash = VirgilHash.sha512();
        byte[] hash = virgilHash.hash(TEST_STRING.getBytes());
        assertEquals("1e3bb3112ed0ee72c342507c19bcea26901789dd5404cb0932ebaa44ef468fea9862883f85368c85b2b8039899838b5c0b46a64f56c118c3dc44c6052acca2c5", hashToString(hash));
    }
    
    @Test
    public void testMd5String() {
        VirgilHash virgilHash = VirgilHash.md5();
        byte[] hash = virgilHash.hash(TEST_STRING.getBytes());
        assertEquals("94c2f952e7f8f0116034edce9b0e5fe9", hashToString(hash));
    }
    
    private String hashToString(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash).toLowerCase();
    }
    
}
