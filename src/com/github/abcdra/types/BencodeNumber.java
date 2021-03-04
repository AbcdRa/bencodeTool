package com.github.abcdra.types;

public class BencodeNumber implements BencodeType {
    private final String value;
    public final Number raw;

    @Override
    public BRawType type() {
        return BRawType.NUMBER;
    }

    public BencodeNumber(Number number) {
        raw = number;
        value = "i"+number+"e";
    }

    @Override
    public Object raw() {
        return raw;
    }

    @Override
    public String value() {
        return value;
    }
}