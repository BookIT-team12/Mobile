package com.example.bookit.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class StringImageConverter {
    public static List<byte[]> turnStringsToBytes(List<String> based64Strings){
        List<byte[]> retVal = new ArrayList<>();
        for (String s:based64Strings){
            byte[] decodedBytes = Base64.getDecoder().decode(s);
            retVal.add(decodedBytes);
        }
        return retVal;
    }
}
