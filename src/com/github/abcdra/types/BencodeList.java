package com.github.abcdra.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BencodeList implements BencodeType{
    private final String value;
    private final List<BencodeType> raw;

    @Override
    public BRawType type() {
        return BRawType.LIST;
    }

    @Override
    public Object raw() {
        return raw;
    }

//    public BencodeList(BencodeType[] arr) {
//        List<BencodeType> list = new ArrayList<>(Arrays.asList(arr));
//        raw = list;
//        StringBuilder acc = new StringBuilder("l");
//        for (BencodeType bencodeType : list) {
//            acc.append(bencodeType.value());
//        }
//        value = acc.append("e").toString();
//    }

    public BencodeList(List<BencodeType> list) {
        raw = list;
        StringBuilder acc = new StringBuilder("l");
        for (BencodeType bencodeType : list) {
            acc.append(bencodeType.value());
        }
        value = acc.append("e").toString();
    }

    @Override
    public String value() {
        return value;
    }
    public List<BencodeType> getRaw(){
        return raw;
    }
}
