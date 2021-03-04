package com.github.abcdra.types;

enum BRawType {NUMBER,STRING,DICT,LIST,RAW}

public interface BencodeType {
    String value();
    Object raw();
    BRawType type();
}
