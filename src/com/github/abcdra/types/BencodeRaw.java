package com.github.abcdra.types;

public class BencodeRaw implements BencodeType {
    private final String raw;
    private final String value;

    public BencodeRaw(String raw) {
        this.raw = raw;
        this.value = raw;
    }
    @Override
    public String value() {
        return value;
    }

    @Override
    public Object raw() {
        return raw;
    }

    @Override
    public BRawType type() {
        return BRawType.RAW;
    }
}
