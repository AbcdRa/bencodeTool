package com.github.abcdra.serialization;

public class JSONToBencode {
    public static String convert(String json) {
        json = json.trim();
        StringBuilder bencode = new StringBuilder();
        boolean insideString =false;
        boolean insideNumber = false;
        StringBuilder currentString = new StringBuilder();
        for(var i=0; i<json.length();i++) {
            if(json.charAt(i)=='[' && !insideString) {
                bencode.append('l');
                continue;
            }
            if((json.charAt(i)==']' || json.charAt(i)=='}') && !insideString) {
                bencode.append('e');
                continue;
            }
            if(json.charAt(i)==' ' || json.charAt(i)==':' || json.charAt(i)=='-' || json.charAt(i)==',' && !insideString) {
                if(insideNumber) {
                    bencode.append('e');
                    insideNumber = false;
                }
                continue;
            }
            if(json.charAt(i)=='{' && !insideString) {
                bencode.append('d');
                continue;
            }
            if (json.substring(i,i+1).matches("^[1-9]+") && !insideString && !insideNumber) {
                if(i!=0&&json.charAt(i-1)=='-') {
                    bencode.append("i-");
                } else {
                    bencode.append('i');
                }
                bencode.append(json.charAt(i));
                insideNumber = true;
                continue;
            }
            if(json.charAt(i)=='"' && !insideString) {
                insideString = true;
                currentString = new StringBuilder();
                continue;
            }
            if(insideString && json.charAt(i)!='"') {
                currentString.append(json.charAt(i));
                continue;
            }
            if (insideString && json.charAt(i)=='"'){
                bencode.append(currentString.length()).append(':').append(currentString.toString());
                insideString = false;
            }
        }
        return bencode.toString();
    }

}
