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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.IOUtils;

/**
 *
 * @author Andrii Iakovenko
 */
public class GenericSamplesTest {
    
    private static final Logger LOGGER = Logger.getLogger(GenericSamplesTest.class.getName());
    
    public static final String SAMPLES_DIR = "com/virgilsecurity/sdk/crypto/samples/";
    
    /*
     * Converts array of bytes to hex String.
     * @param bytes array of bytes to be converted to hex String.
     * @return 
     */
    public static String bytesToHex(byte[] bytes) {
        if ((bytes == null) || (bytes.length == 0)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
    
    /*
     * Converts array of bytes to String.
     * @param bytes array of bytes to be converted to String.
     * @return 
     */
    public static String bytesToString(byte[] bytes) {
        if ((bytes == null) || (bytes.length == 0)) {
            return "";
        }
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    //        
    
    /*
    * Reads resource to array of bytes.
    * @param resourceName resource file name
    * returns array of bytes read from resource. If resource not found returns <code>null</code>.
    */
    public byte[] readResource(String resourceName) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
            if (is != null) {
                return IOUtils.readFully(is, -1, true);
            }
            LOGGER.severe(String.format("Resource %s not found.", resourceName));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /*
    * Reads resource as String.
    * @param resourceName resource file name
    * returns String read from resource. If resource not found returns <code>null</code>.
    */
    public String readTextResource(String resourceName) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
            if (is != null) {
                return org.apache.commons.io.IOUtils.toString(is);
            }
            LOGGER.severe(String.format("Resource %s not found.", resourceName));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String[] getAllSamples() {
        return new String[]{"sample_0.txt", "sample_1.txt"};
    }
    
}
