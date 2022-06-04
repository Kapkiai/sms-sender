package com.kapkiai.smpp.utils;

/**
 * Support functions for UCS-2 encodings.
 * <p>
 * UCS-2 is subset of the UTF-16BE charset, only the Basic Multilingual Plane codepoints are encodable as UCS-2.
 */
public class Ucs2 {

    /**
     * Verify is the java string consists of UCS-2 characters, i.e. in the Basic Multilingual Plane of Unicode
     */
    public static boolean isUcs2Encodable(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isBmpCodePoint(s.codePointAt(i))) {
                return false;
            }
        }
        return true;
    }

}
