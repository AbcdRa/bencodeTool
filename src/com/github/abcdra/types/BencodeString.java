package com.github.abcdra.types;

import com.github.abcdra.types.BencodeType;

public class BencodeString implements BencodeType {
    private final String value;
    public final String raw;

    public BencodeString(String str) {
        raw = str;
        value = str.length()+":"+str;
    }

    @Override
    public BRawType type() {
        return BRawType.STRING;
    }



    @Override
    public String value() {
        return value;
    }

    @Override
    public Object raw() {
        return raw;
    }
}