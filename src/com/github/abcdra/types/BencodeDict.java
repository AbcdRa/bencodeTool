package com.github.abcdra.types;

import java.util.*;

public class BencodeDict implements BencodeType{
    private final String value;
    public final Map<BencodeString, BencodeType> raw;

    @Override
    public Object raw() {
        return raw;
    }

    @Override
    public BRawType type() {
        return BRawType.DICT;
    }

    public BencodeDict(Map<BencodeString, BencodeType> dict) {
        raw = dict;
        //Необходимо обеспечить лексикографический порядок в словаре
        Set<BencodeString> keys = dict.keySet();
        //Поэтому список ключей преобразуются в лист, который можно сортировать
        List<BencodeString> keysList = new ArrayList<>(keys);
        keysList.sort(Comparator.comparing((BencodeString s) -> s.raw));

        StringBuilder acc = new StringBuilder("d");
        for(BencodeString key : keysList) {
            acc.append(key.value()).append(dict.get(key).value());
        }

        value = acc.append("e").toString();
    }

    @Override
    public String value() {
        return value;
    }
}
