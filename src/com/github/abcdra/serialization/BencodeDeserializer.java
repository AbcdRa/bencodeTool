package com.github.abcdra.serialization;

import com.github.abcdra.types.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BencodeDeserializer {
    public static BencodeType deserialize(String str) throws ParseException {
        str = str.trim();
        if(str.charAt(0) == 'i') {
            return new BencodeNumber(rawParseNum(str).first);
        }
        else if (str.substring(0,1).matches("^[1-9]+")) {
            return new BencodeString(rawParseStr(str).first);
        }
        else if(str.charAt(0)=='l') {
            return rawParseList(str).first;
        }
        else if(str.charAt(0)=='d'){
            return rawParseDict(str).first;
        }
        throw new ParseException("Error with first symbol",0);
    }


    private static Dual<BencodeList,String> rawParseList(String str) throws ParseException {
        String c_str = str.substring(1);
        List<BencodeType> arr = new ArrayList<>();
        while (!c_str.equals("") && c_str.charAt(0) != 'e') {
            if (c_str.charAt(0) == 'i') {
                Dual<Number, String> rawParses = rawParseNum(c_str);
                arr.add(new BencodeNumber(rawParses.first));
                c_str = rawParses.second;
            }
            if(c_str.substring(0,1).matches("^[1-9]+"))  {
                Dual<String, String> rawParses = rawParseStr(c_str);
                arr.add(new BencodeString(rawParses.first));
                c_str = rawParses.second;
            }
            if(c_str.charAt(0)=='l') {
                Dual<BencodeList, String> rawParses = rawParseList(c_str);
                arr.add(rawParses.first);
                c_str = rawParses.second;
            }
            if(c_str.charAt(0)=='d') {
                Dual<BencodeDict, String> rawParses = rawParseDict(c_str);
                arr.add(rawParses.first);
                c_str = rawParses.second;
            }
        }
        BencodeList bencodeList = new BencodeList(arr);
        return new Dual<>(bencodeList,c_str.substring(1));
    }


    private static Dual<BencodeDict,String> rawParseDict(String str) throws ParseException {
        String c_str = str.substring(1);
        Map<BencodeString,BencodeType> dict = new HashMap<>();
        boolean isKey = true;
        BencodeString key = null;
        while (!c_str.equals("") && c_str.charAt(0) != 'e') {
            if (c_str.charAt(0) == 'i' ) {
                if(isKey)
                    throw new ParseException("Error in raw dict parse, key is not bencode string"+c_str.substring(10),0);
                Dual<Number,String> rawParses = rawParseNum(c_str);
                dict.put(key, new BencodeNumber(rawParses.first));
                c_str = rawParses.second;
                isKey = true;
            }
            if(c_str.substring(0,1).matches("^[1-9]+"))  {
                Dual<String, String> rawParses = rawParseStr(c_str);
                if(isKey) {
                    key = new BencodeString(rawParses.first);
                    isKey = false;
                }
                else {
                    dict.put(key, new BencodeString(rawParses.first));
                    isKey = true;
                }
                c_str = rawParses.second;
            }
            if(c_str.charAt(0)=='l') {
                if(isKey)
                    throw new ParseException("Error in raw dict parse, key is not bencode string"+c_str.substring(10),0);
                Dual<BencodeList, String> rawParses = rawParseList(c_str);
                dict.put(key, rawParses.first);
                isKey = true;
                c_str = rawParses.second;
            }
            if(c_str.charAt(0)=='d') {
                if(isKey)
                    throw new ParseException("Error in raw dict parse, key is not bencode string"+c_str.substring(10),0);
                Dual<BencodeDict, String> rawParses = rawParseDict(c_str);
                dict.put(key, rawParses.first);
                isKey = true;
                c_str = rawParses.second;
            }
        }
        BencodeDict bencodeDict = new BencodeDict(dict);
        return new Dual<>(bencodeDict,c_str.substring(1));
    }


    //Берет строку содержащую bencode_string и возвращает распарсенную bencode_string и вырезанную из основной строки
    //bencode строки
    private static Dual<String, String> rawParseStr(String str) throws ParseException {
        int colon_index = str.indexOf(":");
        if (colon_index==-1) throw new ParseException("Error in raw string parse, cannot find colon ':'",1);
        String s_num = str.substring(0, colon_index);
        int length;
        try {
            length = Integer.parseInt(s_num);
        } catch (NumberFormatException e) {
            throw new ParseException("Error in raw string parse, cannot recognize as Number " + s_num,1);
        }
        String parse_str;
        try {
            parse_str = str.substring(colon_index+1,colon_index+length+1);
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Error in raw string parse, line break " + s_num,2);
        }
        String strWithoutBencodeString = str.substring(colon_index+length+1);
        return new Dual<>(parse_str, strWithoutBencodeString);
    }


    private static Dual<Number, String> rawParseNum(String str) throws ParseException {
        int end_index = str.indexOf('e');
        if(end_index == -1) throw new ParseException("Error in raw number parse, cannot find number 'e'",0);
        String s_num = str.substring(1,end_index);
        Number num;
        try {
            num = Long.parseLong(s_num);
        } catch (NumberFormatException e) {
            throw new ParseException("Error in raw number parsing... Cannot recognize = "+s_num+" as number",0);
        }
        return new Dual<>(num,str.substring(end_index+1));
    }
}

