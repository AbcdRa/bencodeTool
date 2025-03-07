package com.github.abcdra.serialization;

import com.github.abcdra.types.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BencodeSerializer {

    public static String serialize(String str) {
        return new BencodeString(str).value();
    }

    public static String serialize(Number num) {
        return new BencodeNumber(num).value();
    }


    public static <T> String serialize(List<T> list) {
        if(list.size()==0) return "le";
        if(list.get(0) instanceof String) {
            List<BencodeType> arr = new ArrayList<>();
            list.forEach(v->arr.add(new BencodeString((String) v)));
            return new BencodeList(arr).value();
        }
        if(list.get(0) instanceof Number) {
            List<BencodeType> arr = new ArrayList<>();
            list.forEach(v->arr.add(new BencodeNumber((Number) v)));
            return new BencodeList(arr).value();
        }
        if(list.get(0) instanceof List<?>) {
            List<String> arr = new ArrayList<>();
            list.forEach(v->arr.add(serialize((List<?>) v)) );
            StringBuilder acc = new StringBuilder("l");
            arr.forEach(acc::append);
            return acc+"e";
        }
        if(list.get(0) instanceof Map<?,?>) {
            List<String> arr = new ArrayList<>();
            try {
                list.forEach(v->arr.add(serialize((Map<String, Object>) v)) );
            } catch (ClassCastException ex) {
                System.err.println("Cast failed " + ex.getMessage());
                return "le";
            }

            StringBuilder acc = new StringBuilder("l");
            arr.forEach(acc::append);
            return acc+"e";
        }
        return "le";
    }

    public static <T> String serialize(Map<String, T> dict) {
        String[] keyArr = dict.keySet().toArray(new String[0]);
        if(keyArr.length == 0) return "de";
        String testKey = keyArr[0];
        if(dict.get(testKey) instanceof Number) {
            Map<BencodeString, BencodeType> bDict = new HashMap<>();
            dict.forEach((k,v)->bDict.put(new BencodeString(k), new BencodeNumber((Number) v)));
            return new BencodeDict(bDict).value();
        }
        if(dict.get(testKey) instanceof String) {
            Map<BencodeString, BencodeType> bDict = new HashMap<>();
            dict.forEach((k,v)->bDict.put(new BencodeString(k), new BencodeString((String) v)));
            return new BencodeDict(bDict).value();
        }
        if(dict.get(testKey) instanceof List<?>) {
            Map<BencodeString, BencodeType> bDict = new HashMap<>();
            dict.forEach((k,v)-> {
                String serializedList = serialize((List<?>) v);
                bDict.put(new BencodeString(k), new BencodeRaw(serializedList));
            });

            return new BencodeDict(bDict).value();
        }
        if(dict.get(testKey) instanceof Map<?,?>) {
            Map<BencodeString, BencodeType> bDict = new HashMap<>();
            try {
                dict.forEach((k,v)-> {
                    String serializedDict = serialize((Map<String,?>) v);
                    bDict.put(new BencodeString(k), new BencodeRaw(serializedDict));
                });
            } catch (ClassCastException ex) {
                System.err.println("Cast failed" + ex.getMessage());
            }
            return new BencodeDict(bDict).value();
        }
        return "de";
    }

}
