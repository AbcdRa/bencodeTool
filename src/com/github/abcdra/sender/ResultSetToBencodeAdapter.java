package com.github.abcdra.sender;

import com.github.abcdra.types.BencodeType;

import java.sql.ResultSet;

public interface ResultSetToBencodeAdapter {
    BencodeType convert(ResultSet resultSet);
}
