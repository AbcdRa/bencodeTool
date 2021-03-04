package com.github.abcdra.sender;

import com.github.abcdra.types.BencodeType;

public interface BencodeSender {
    //Отправить bencode файл к источнику
    void send(BencodeType content);
    void setSource(String source);
}
