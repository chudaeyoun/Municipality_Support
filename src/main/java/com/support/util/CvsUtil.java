package com.support.util;

public class CvsUtil {

    //csv 콤마로 구분하여 짜르기
    public String[] csvSplit(String str) {
        String[] resultStr = null;
        String result = "";

        String[] a = str.split(",");

        int cnt = 0;

        String temp = "";

        for(int i = 0; i < a.length; i++) {
            if(a[i].indexOf("\"") == 0) {
                if(a[i].lastIndexOf("\"") == a[i].length() - 1) {
                    result += a[i].replaceAll("\"","");
                } else {
                    cnt++;
                    temp += a[i].replaceAll("\"","");
                }
            } else if(a[i].lastIndexOf("\"") == a[i].length() - 1) {
                if(cnt > 0) {
                    result += temp + "," + a[i].replaceAll("\"","");
                    cnt = 0;
                    temp = "";
                }
            } else {
                if(cnt > 0) {
                    cnt++;
                    temp += "," + a[i].replaceAll("\"","");
                } else {
                    result += a[i];
                }
            }

            if (i != a.length - 1 && cnt == 0) {
                result += "|,|";
            }
        }

        resultStr = result.split("\\|,\\|");

        return resultStr;
    }
}
