package com.zm.qconfigdemo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class CommonFunc {

    public static String inputStream2String(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static boolean isAvaliableAppid(String appid){
        Pattern p = Pattern.compile("[0-9]{1,15}");
        return p.matcher(appid).matches();
    }

}
